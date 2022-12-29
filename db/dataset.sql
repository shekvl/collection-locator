-- get random rows
select * from cdm.concept
ORDER BY random()
LIMIT 100

select * from cdm.concept
where vocabulary_id in ('LOINC', 'SNOMED', 'READ')
and domain_id in ('Measurement', 'Observation')
ORDER BY random()
LIMIT 10

-- check out what standard vocab means and the validity attribute D/U
select concept_id, concept_name, vocabulary_id, concept_code from cdm.concept
where vocabulary_id in ('LOINC', 'SNOMED', 'Read')
and domain_id in ('Measurement', 'Observation')
and invalid_reason = ''
ORDER BY random()
LIMIT 50

-- just LOINC Measurements
select * from (
	select (random() + random()) /2 * 0.3 + 0.7, concept_id, concept_name, vocabulary_id, concept_code from cdm.concept
	where vocabulary_id in ('LOINC')
	and domain_id in ('Measurement')
	and invalid_reason = '') as a
ORDER BY random()
LIMIT 20


-- round() for real data type
CREATE OR REPLACE FUNCTION round(number real, decimal_places int)
RETURNS real AS $$
	BEGIN
		RETURN ROUND(number::numeric, decimal_places)::real;
	END;
$$ LANGUAGE plpgsql;


-- generate random value between min and max
-- roundet to 2 decimal places
CREATE OR REPLACE FUNCTION random(min real, max real)
RETURNS real AS $$
	DECLARE
		scalar real = max - min;
		number real = random() * scalar + min;
	BEGIN
		RETURN round(number, 2);
	END;
$$ LANGUAGE plpgsql;


-- generate random value between min and max following a gaussian distribution
-- roundet to 2 decimal places
CREATE OR REPLACE FUNCTION random_gaussian(min real, max real)
RETURNS real AS $$
	DECLARE
		scalar real = max - min;
		number real = (random() + random())/2 * scalar + min; --the more random() values are summed up, the closer it gets to normal distribution
	BEGIN
		RETURN round(number, 2);
	END;
$$ LANGUAGE plpgsql;


--generated dataset with attributed collection name
CREATE OR REPLACE FUNCTION generate_dataset(collection_name varchar(255), size int)
RETURNS TABLE (
	collection_name varchar(255),
	attribute_name varchar(255),
	code varchar(50),
	vocabulary_id varchar(20),
	completeness real,
	accuracy real,
	reliability real,
	timeliness real,
	consistancy real
) AS $$
SELECT
	collection_name,
	concept_name,
	concept_code,
	vocabulary_id,
	random(0.3,1),
	random(0.7,1),
	random(0.7,1),
	random(0.7,1),
	random(0.7,1)
FROM
	cdm.concept
WHERE
	vocabulary_id IN ('LOINC')
	AND domain_id IN ('Measurement')
	AND invalid_reason = ''
ORDER BY random()
LIMIT size
$$ LANGUAGE SQL;


--generated dataset
CREATE OR REPLACE FUNCTION generate_dataset(size int)
RETURNS TABLE (
	attribute_name varchar(255),
	code varchar(50),
	vocabulary_id varchar(20),
	completeness real,
	accuracy real,
	reliability real,
	timeliness real,
	consistancy real
) AS $$
SELECT
	concept_name,
	concept_code,
	vocabulary_id,
	random(0.3,1),
	random(0.7,1),
	random(0.7,1),
	random(0.7,1),
	random(0.7,1)
FROM
	cdm.concept
WHERE
	vocabulary_id IN ('LOINC')
	AND domain_id IN ('Measurement')
	AND invalid_reason = ''
ORDER BY random()
LIMIT size
$$ LANGUAGE SQL;

-- generate collection
-- parameters: collection name, number of annotated attributes, the person adding the collection and the institution the collection is associated with
-- 1) create collection and generate collection quality metrics
-- 2) generate table with n random cdm concepts and generates quality metrics for each concept
-- 3) loop through generated table via cursor
-- 4) for each row: insert row into attribute table and concept into attribute_concept table
-- returning collection id, first and last attribute_id of newly created collection
CREATE OR REPLACE FUNCTION generate_collection (collection_name varchar(255), number_of_attributes integer, added_by integer, institution_id varchar(255) default null)
RETURNS table(collection_id integer, first_attribute_id bigint, last_attribute_id bigint) AS $$
	DECLARE
		collection_id integer = (select id
								 from insert_record_collection(collection_name, institution_id, random(20,2500)::int,
														   random_gaussian(0.2,1), random_gaussian(0.65,1),
														   random_gaussian(0.65,1), random_gaussian(0.65,1),
														   random_gaussian(0.65,1), added_by)
								 );
		cursor cursor for select * from generate_dataset(number_of_attributes);
		row record;
		attribute_id bigint;
		first_attribute_id bigint;
		first_iteration boolean = true;
	BEGIN
		open cursor;
		LOOP
			fetch cursor into row;
			exit when not found;
			attribute_id := (select id
							 from insert_record_attribute(collection_id, row.attribute_name, row.completeness, row.accuracy, row.reliability,
														  row.timeliness, row.consistancy)
							);
			perform insert_record_attribute_concept(attribute_id, row.code, row.vocabulary_id);

			IF first_iteration
			THEN
				first_attribute_id = attribute_id;
				first_iteration := false;
			END IF;
		END LOOP;
		close cursor;

		RETURN Query
			select collection_id, first_attribute_id, attribute_id;
	END;
$$ LANGUAGE plpgsql;


--TODO generate_collection from imput data ... probably not necessary
-- make generation downlaodable
-- upload excel sheet (collection sheet, table sheet)