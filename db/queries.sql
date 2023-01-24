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



----- new year ------


-- all relationships between from loinc codes
select c1.concept_name,
c1.vocabulary_id,
c2.concept_name,
c2.vocabulary_id,
r.relationship_id,
r.relationship_concept_id
from
cdm.concept c1,
cdm.concept c2,
cdm.concept_relationship cr,
cdm.relationship r
where
cr.concept_id_1 = c1.concept_id
and cr.concept_id_2 = c2.concept_id
and cr.relationship_id = r.relationship_id

and c1.vocabulary_id = 'LOINC'
and c2.vocabulary_id != 'LOINC'


-- relationship with aggregated values (+)
-- has scale type | 44818773 | {-,*,Doc,Multi,Nar,Nom,Ord,OrdQn,Qn,Set}
select
r.relationship_id,
r.relationship_concept_id,
array_agg(distinct c2.concept_name)
from
cdm.concept c1,
cdm.concept c2,
cdm.concept_relationship cr,
cdm.relationship r
where
cr.concept_id_1 = c1.concept_id
and cr.concept_id_2 = c2.concept_id
and cr.relationship_id = r.relationship_id
and c1.vocabulary_id = 'LOINC'
group by r.relationship_id, r.relationship_concept_id


-- all relationship_ids of LOINC vocabulary_id
select
r.relationship_id,
r.relationship_concept_id
from
cdm.concept c1,
cdm.concept c2,
cdm.concept_relationship cr,
cdm.relationship r
where
cr.concept_id_1 = c1.concept_id
and cr.concept_id_2 = c2.concept_id
and cr.relationship_id = r.relationship_id
and c1.vocabulary_id = 'LOINC'
group by r.relationship_id, r.relationship_concept_id


-- insert relationship into relationship_of_interest
insert into relationship_of_interest("group", "name", "relationship_concept_id", "relationship_id", "distinct_values", "distinct_value_count", "vocabulary_id")
select
'axes',
'scale',
r.relationship_concept_id,
r.relationship_id,
array_agg(distinct c2.concept_name),
count(distinct c2.concept_name),
'LOINC'
from
cdm.concept c1,
cdm.concept c2,
cdm.concept_relationship cr,
cdm.relationship r
where
cr.concept_id_1 = c1.concept_id
and cr.concept_id_2 = c2.concept_id
and cr.relationship_id = r.relationship_id
and c1.vocabulary_id = 'LOINC'
and r.relationship_id = 'Has scale type'
group by r.relationship_id, r.relationship_concept_id
