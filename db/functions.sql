-- Table functions:
-- Get all direct children of concept_id
create
or replace function get_children(concept_id int) RETURNS table (
  Child_concept_id integer,
  Child_concept_name varchar,
  Child_concept_code varchar,
  Child_concept_class_id varchar,
  Child_concept_vocab_ID varchar,
  Child_concept_vocab_name varchar
) as $$
SELECT
  D.concept_id Child_concept_id,
  D.concept_name Child_concept_name,
  D.concept_code Child_concept_code,
  D.concept_class_id Child_concept_class_id,
  D.vocabulary_id Child_concept_vocab_ID,
  VS.vocabulary_name Child_concept_vocab_name
FROM
  cdm.concept_ancestor CA,
  cdm.concept D,
  cdm.vocabulary VS
WHERE
  CA.ancestor_concept_id = $1
  AND CA.min_levels_of_separation = 1
  AND CA.descendant_concept_id = D.concept_id
  AND D.vocabulary_id = VS.vocabulary_id
  AND now() BETWEEN D.valid_start_date
  AND D.valid_end_date;

$$ language sql;



--test querys to find out common relationships .... for development, can be deleted later

SELECT
	CR.relationship_ID,
	RT.relationship_name,
	D.concept_Id concept_id,
	D.concept_Name concept_name,
	D.concept_Code concept_code,
	D.concept_class_id concept_class_id,
	D.vocabulary_id concept_vocab_ID,
	VS.vocabulary_name concept_vocab_name
FROM
	cdm.concept_relationship CR,
	cdm.concept A,
	cdm.concept D,
	cdm.vocabulary VA,
	cdm.vocabulary VS,
	cdm.relationship RT
WHERE CR.concept_id_1 = A.concept_id
AND A.vocabulary_id = VA.vocabulary_id
AND CR.concept_id_2 = D.concept_id
AND D.vocabulary_id = VS.vocabulary_id
AND CR.relationship_id = RT.relationship_ID
AND A.concept_id = 607590
AND now() BETWEEN CR.valid_start_date
AND CR.valid_end_date;


SELECT
	CR.relationship_ID,
	RT.relationship_name,
	D.concept_Id concept_id,
	D.concept_Name concept_name,
	D.concept_Code concept_code,
	D.concept_class_id concept_class_id,
	D.vocabulary_id concept_vocab_ID,
	VS.vocabulary_name concept_vocab_name
FROM
	cdm.concept_relationship CR,
	cdm.concept A,
	cdm.concept D,
	cdm.vocabulary VA,
	cdm.vocabulary VS,
	cdm.relationship RT
WHERE CR.concept_id_1 = A.concept_id
AND A.vocabulary_id = VA.vocabulary_id
AND CR.concept_id_2 = D.concept_id
AND D.vocabulary_id = VS.vocabulary_id
AND CR.relationship_id = RT.relationship_ID
AND A.concept_id = 40767344
AND now() BETWEEN CR.valid_start_date
AND CR.valid_end_date;

//ancestors:
SELECT
	C.concept_id as ancestor_concept_id,
	C.concept_name as ancestor_concept_name,
	C.concept_code as ancestor_concept_code,
	C.concept_class_id as ancestor_concept_class_id,
	C.vocabulary_id,
	VA.vocabulary_name,
	A.min_levels_of_separation,
	A.max_levels_of_separation
FROM
	cdm.concept_ancestor A,
	cdm.concept C,
	cdm.vocabulary VA
WHERE
	A.ancestor_concept_id = C.concept_id
	AND C.vocabulary_id = VA.vocabulary_id
	AND A.ancestor_concept_id<>A.descendant_concept_id
	AND A.descendant_concept_id = 192671
	AND now() BETWEEN valid_start_date
	AND valid_end_date
ORDER BY 5,7;



--
separate function for getting children -> in(...)


--> maybe also a view where you see all collections and a list of each of their annotation (see, whats there practically) vs. search loinc vs browser trea or something

-->  find common parent ?! / store tree as json in db? // js tree structure? to navigate, delete, append, search, etc..
--https://stackoverflow.com/questions/5003028/how-can-i-fill-a-column-with-random-numbers-in-sql-i-get-the-same-value-in-ever#5003054
--make creation directly in db functions
-- how to download?


