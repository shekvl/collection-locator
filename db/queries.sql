-- cdm specific retrieval queries

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



-- query loinc with parent (inclusive)
-- composite search (ALL)
-- query loinc parts (for specific onthology)


view:
-- get all vocabs OK
-- get all concepts ??
-- get all collections ??