-- Update LAST_MODIFIED on row update
CREATE OR REPLACE FUNCTION update_last_modified ()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified = NOW ();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON person
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON collection
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON institution
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON dataset_attribute
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON password
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();

CREATE TRIGGER update_last_modified
	BEFORE UPDATE ON session
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified ();


-- INSERT CONCEPT_ID from OMOP CDM into cdm_concept table
CREATE OR REPLACE FUNCTION insert_cdm_concept ()
RETURNS TRIGGER AS $$
BEGIN
    NEW.concept_id = (
        select concept.concept_id
        from "omop_cdm_5.4".concept as concept
        where concept.vocabulary_id = NEW.vocabulary_id
        and concept.concept_code = NEW.code
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER insert_cdm_concept
	BEFORE INSERT ON cdm_concept
    FOR EACH ROW
    EXECUTE FUNCTION insert_cdm_concept ();


-- Insert CODE and VOCABULARY_ID into cdm_concept table
-- Assert that concept exists, fail otherwise
CREATE OR REPLACE FUNCTION transfer_annotation ()
RETURNS TRIGGER AS $$
DECLARE
	 concept integer := (select concept.concept_id
                        from "omop_cdm_5.4".concept as concept
                        where concept.vocabulary_id = NEW.vocabulary_id
                        and concept.concept_code = NEW.code);
BEGIN
	IF concept IS NOT NULL
	THEN
		EXECUTE FORMAT('insert into cdm_concept values($1, $2) ON CONFLICT DO NOTHING')
		USING NEW.code, NEW.vocabulary_id;
		RETURN NEW;
	ELSE
		RETURN null;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER transfer_annotation
	BEFORE INSERT ON attribute_concept
    FOR EACH ROW
    EXECUTE FUNCTION transfer_annotation ();


-- Set priviously ACTIVE password to unactive when new password is inserted
CREATE OR REPLACE FUNCTION inactivate_old_password ()
RETURNS TRIGGER AS $$
BEGIN
	update password
	set active = false
	where active IS TRUE
	and person_id = NEW.person_id
	and password_hash != NEW.password_hash;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER inactivate_old_password
	AFTER INSERT ON password
    FOR EACH ROW
    EXECUTE FUNCTION inactivate_old_password ();

-- Delete older passwords with offset when new password is inserted
CREATE OR REPLACE FUNCTION keep_last_3_passwords ()
RETURNS TRIGGER AS $$
BEGIN
	delete from password
	where person_id = NEW.person_id
	and password_hash in (
		select password_hash
		from password
		where person_id = NEW.person_id
		order by created_at desc
		offset 3
	);
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER keep_last_3_passwords
	AFTER INSERT ON password
    FOR EACH ROW
    EXECUTE FUNCTION keep_last_3_passwords ();