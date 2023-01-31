-- Update LAST_MODIFIED on row update
CREATE OR REPLACE FUNCTION update_last_modified ()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified = NOW ();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_last_modified ON person;
CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON person
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

DROP TRIGGER IF EXISTS update_last_modified ON collection;
CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON collection
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

DROP TRIGGER IF EXISTS update_last_modified ON institution;
CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON institution
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

DROP TRIGGER IF EXISTS update_last_modified ON attribute;
CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON attribute
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

DROP TRIGGER IF EXISTS update_last_modified ON relationship_of_interest;
CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON relationship_of_interest
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();


-- INSERT CONCEPT_ID from OMOP CDM into concept table
CREATE OR REPLACE FUNCTION insert_concept ()
RETURNS TRIGGER AS $$
BEGIN
    NEW.concept_id = (
        select concept.concept_id
        from cdm.concept as concept
        where concept.vocabulary_id = NEW.vocabulary_id
        and concept.concept_code = NEW.code
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_concept ON concept;
CREATE TRIGGER insert_concept
	BEFORE INSERT ON concept
    FOR EACH ROW
    EXECUTE FUNCTION insert_concept ();


-- Insert CODE and VOCABULARY_ID into concept table
-- Assert that concept exists, fail otherwise
CREATE OR REPLACE FUNCTION transfer_annotation ()
RETURNS TRIGGER AS $$
DECLARE
	 concept integer := (select concept.concept_id
                        from cdm.concept as concept
                        where concept.vocabulary_id = NEW.vocabulary_id
                        and concept.concept_code = NEW.code);
BEGIN
	IF concept IS NOT NULL
	THEN
		EXECUTE FORMAT('insert into concept (code, vocabulary_id, reference_count) values($1, $2, 1) ON CONFLICT DO NOTHING')
		USING NEW.code, NEW.vocabulary_id;
		RETURN NEW;
	ELSE
		RETURN null;
	END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS transfer_annotation ON attribute_concept;
CREATE TRIGGER transfer_annotation
	AFTER INSERT ON attribute_concept  -- BEFORE INSERT would prevent unsupported vocabularies
    FOR EACH ROW
    EXECUTE FUNCTION transfer_annotation ();


-- keep count of current references to know if concept still present in collections
CREATE OR REPLACE FUNCTION increment_concept_reference_count ()
RETURNS TRIGGER AS $$
BEGIN
	update concept
	set reference_count = reference_count + 1
	where code = NEW.code
	and vocabulary_id = NEW.vocabulary_id;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS increment_concept_reference_count ON attribute_concept;
CREATE TRIGGER increment_concept_reference_count
	BEFORE INSERT ON attribute_concept
    FOR EACH ROW
	EXECUTE FUNCTION increment_concept_reference_count();


-- keep count of current references to know if concept still present in collections
CREATE OR REPLACE FUNCTION decrement_concept_reference_count ()
RETURNS TRIGGER AS $$
BEGIN
	update concept
	set reference_count = reference_count - 1
	where code = OLD.code
	and vocabulary_id = OLD.vocabulary_id;
	RETURN OLD;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS decrement_concept_reference_count ON attribute_concept;
CREATE TRIGGER decrement_concept_reference_count
	AFTER DELETE ON attribute_concept
    FOR EACH ROW
	EXECUTE FUNCTION decrement_concept_reference_count();


-- delete concept if not referenced anymore
CREATE OR REPLACE FUNCTION delete_unreferenced_concept_record ()
RETURNS TRIGGER AS $$
BEGIN
	IF NEW.reference_count <= 0
	THEN
		EXECUTE FORMAT('delete from concept where concept_id = $1')
		USING OLD.concept_id;
		RETURN OLD;
	ELSE
		RETURN null;
	END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS delete_unreferenced_concept_record ON concept;
CREATE TRIGGER delete_unreferenced_concept_record
	AFTER UPDATE ON concept
    FOR EACH ROW
	EXECUTE FUNCTION delete_unreferenced_concept_record();


-- DEBUG:
-- Raise info '%',OLD;
