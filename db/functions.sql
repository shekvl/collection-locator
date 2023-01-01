-- Table functions for cdm queries:



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



separate function for getting children -> in(...)






-- Get attribute count for each collection of a list
create
or replace function get_attribute_counts(collection_ids int[]) RETURNS table (
  collection_id integer,
  collection_name varchar(255),
  attribute_count bigint
) as $$
SELECT c.id, c.name, count(a.id)
FROM attribute a, collection c
WHERE a.collection_id = c.id
AND c.id = ANY (collection_ids)
GROUP BY c.id
ORDER BY c.id ASC
$$ language sql;
