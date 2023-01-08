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


-- get collections that contains any concept of a list of concepts
create
or replace function queryAny(concept_ids int[]) RETURNS table (
	id integer,
  	name varchar(255),
  	institution_id varchar(255),
  	number_of_records integer,
  	completeness real,
  	accuracy real,
  	realiability real,
  	timeliness real,
  	consistancy real,
	added_by integer,
	person_name varchar(255)
) as $$
select c.id, c.name, c.institution_id, c.number_of_records, c.completeness, c.accuracy,
c.reliability, c.timeliness, c.consistancy, c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
from collection c, attribute a, attribute_concept ac, concept cn, person p
where c.id = a.collection_id
and a.id = ac.attribute_id
and cn.code = ac.code
and cn.vocabulary_id = ac.vocabulary_id
and p.id = c.added_by
and cn.concept_id = ANY (concept_ids)
$$ language sql;


-- fetch all attributes for each collection_id in the list, including code, vocab and concept_id
create
or replace function queryAttributes(collection_ids int[]) RETURNS table (
	collection_id integer,
	concept_id integer,
  	code varchar(50),
  	vocabulary_id varchar(20),
  	attribute_name varchar(255),
  	completeness real,
  	accuracy real,
  	realiability real,
  	timeliness real,
  	consistancy real
) as $$
select a.collection_id, cn.concept_id, cn.code, cn.vocabulary_id, a.attribute_name, a.completeness, a.accuracy, a.reliability, a.timeliness, a.consistancy
from collection c, attribute a, attribute_concept ac, concept cn
where c.id = a.collection_id
and a.id = ac.attribute_id
and cn.code = ac.code
and cn.vocabulary_id = ac.vocabulary_id
and c.id = ANY (collection_ids)
$$ language sql;