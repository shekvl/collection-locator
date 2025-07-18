-- Table functions


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
or replace function query_any(concept_ids int[]) RETURNS table (
  id integer,
  name varchar(255),
  institution_id varchar(255),
  number_of_records integer,
  completeness real,
  accuracy real,
  reliability real,
  timeliness real,
  consistancy real,
	added_by integer,
	person_name varchar(255)
) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records, c.completeness, c.accuracy,
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
or replace function query_attributes(collection_ids int[]) RETURNS table (
	collection_id integer,
	concept_id integer,
  	code varchar(50),
  	vocabulary_id varchar(20),
  	attribute_name varchar(255),
  	completeness real,
  	accuracy real,
  	reliability real,
  	timeliness real,
  	consistancy real
) as $$
select distinct a.collection_id, cn.concept_id, cn.code, cn.vocabulary_id, a.attribute_name, a.completeness, a.accuracy, a.reliability, a.timeliness, a.consistancy
from collection c, attribute a, attribute_concept ac, concept cn
where c.id = a.collection_id
and a.id = ac.attribute_id
and cn.code = ac.code
and cn.vocabulary_id = ac.vocabulary_id
and c.id = ANY (collection_ids)
$$ language sql;


-- return all concepts that have a 'Maps to' relationship to any of the concept_ids from the passed array
create
or replace function get_maps(concept_ids int[]) RETURNS table (
	concept_id int
) as $$
	select distinct c2.concept_id
	from cdm.concept_relationship cr
	join cdm.concept c1 ON cr.concept_id_1 = c1.concept_id
	join cdm.concept c2 ON cr.concept_id_2 = c2.concept_id
	where cr.relationship_id in ('Maps to', 'Mapped from')
	and c1.concept_id = any(concept_ids);
$$ language sql;


-- returns all descending concept_ids of parent concept_id
create
or replace function get_descendents(concept_ids int[]) RETURNS table (
	concept_id int
) as $$
	select distinct c.concept_id
	from cdm.concept_ancestor a, cdm.concept c
	where a.descendant_concept_id = c.concept_id
	and a.ancestor_concept_id <> a.descendant_concept_id
	and a.ancestor_concept_id = any (concept_ids);
$$ language sql;

-- drop function get_quality_values_for_collections;

create or replace function get_quality_values_for_collections(collection_ids int[])
    returns TABLE(
        collection_id integer, quality_characteristic_name character varying, friendly_name character varying, quality_value real
     )
    as $$
SELECT c.id, q.name, q.friendly_name, qmc.quality_characteristic_value_for_collection
FROM collection c, quality_characteristic q, quality_characteristic_collection qmc
WHERE c.id=qmc.collection_id and qmc.quality_characteristic_id=q.id
  AND c.id = ANY (collection_ids)
ORDER BY c.id ASC
$$ language sql;

alter function get_quality_values_for_collections(collection_ids int[]) owner to bbmri;

create or replace function get_quality_values_for_attributes(collection_ids int[])
    returns TABLE(
                     collection_id integer, attribute_id bigint, attribute_name character varying,
                     quality_characteristic_name character varying, friendly_name character varying, quality_value real
                 )
as $$
SELECT c.id, a.id, a.attribute_name, q.name, q.friendly_name, qa.quality_characteristic_value_for_attribute
FROM collection c, quality_characteristic q, quality_characteristic_attribute qa, attribute a
WHERE c.id=a.collection_id and a.id=qa.attribute_id and qa.quality_characteristic_id=q.id
  AND c.id = ANY (collection_ids)
ORDER BY c.id ASC
$$ language sql;

alter function get_quality_values_for_attributes(collection_ids int[]) owner to bbmri;

create
or replace function query_collections_by_quality(from1 real, to1 real, q_id int) RETURNS table (
  id integer,
  name varchar(255),
  institution_id varchar(255),
  number_of_records integer,
    added_by integer,
	person_name varchar(255)
) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, quality_characteristic q, quality_characteristic_collection qmc, person p
WHERE c.id=qmc.collection_id and qmc.quality_characteristic_id=q.id
  and p.id = c.added_by
and qmc.quality_characteristic_value_for_collection between from1 and to1
    and q.id=q_id
    $$ language sql;

alter function query_collections_by_quality(from1 real, to1 real, q_id int) owner to bbmri;

create
or replace function query_collections_by_attribute_quality_any(concept_ids int[], from1 real, to1 real, q_id int) RETURNS table (
  id integer,
  name varchar(255),
  institution_id varchar(255),
  number_of_records integer,
    added_by integer,
	person_name varchar(255)
) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, attribute a, attribute_concept ac, concept cn, quality_characteristic q,
     quality_characteristic_attribute qma, person p
WHERE c.id = a.collection_id
  and a.id = ac.attribute_id
  and cn.code = ac.code
  and cn.vocabulary_id = ac.vocabulary_id
  and cn.concept_id = ANY (concept_ids)
  and a.id=qma.attribute_id
  and q.id=q_id
  and qma.quality_characteristic_id=q.id
  and p.id = c.added_by
  and qma.quality_characteristic_value_for_attribute between from1 and to1
    $$ language sql;

alter function query_collections_by_attribute_quality_any(concept_ids int[], from1 real, to1 real, q_id int) owner to bbmri;

create
or replace function query_collections_by_attribute_quality_any_from(concept_ids int[], from1 real, q_id int) RETURNS table (
  id integer,
  name varchar(255),
  institution_id varchar(255),
  number_of_records integer,
    added_by integer,
	person_name varchar(255)
) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, attribute a, attribute_concept ac, concept cn, quality_characteristic q,
     quality_characteristic_attribute qma, person p
WHERE c.id = a.collection_id
  and a.id = ac.attribute_id
  and cn.code = ac.code
  and cn.vocabulary_id = ac.vocabulary_id
  and cn.concept_id = ANY (concept_ids)
  and a.id=qma.attribute_id
  and q.id=q_id
  and qma.quality_characteristic_id=q.id
  and p.id = c.added_by
  and qma.quality_characteristic_value_for_attribute >= from1
    $$ language sql;

alter function query_collections_by_attribute_quality_any_from(concept_ids int[], from1 real, q_id int) owner to bbmri;


create
or replace function query_collections_by_attribute_quality_any_to(concept_ids int[], to1 real, q_id int) RETURNS table (
  id integer,
  name varchar(255),
  institution_id varchar(255),
  number_of_records integer,
    added_by integer,
	person_name varchar(255)
) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, attribute a, attribute_concept ac, concept cn, quality_characteristic q,
     quality_characteristic_attribute qma, person p
WHERE c.id = a.collection_id
  and a.id = ac.attribute_id
  and cn.code = ac.code
  and cn.vocabulary_id = ac.vocabulary_id
  and cn.concept_id = ANY (concept_ids)
  and a.id=qma.attribute_id
  and q.id=q_id
  and qma.quality_characteristic_id=q.id
  and p.id = c.added_by
  and qma.quality_characteristic_value_for_attribute <= to1
    $$ language sql;

alter function query_collections_by_attribute_quality_any_to(concept_ids int[], to1 real, q_id int) owner to bbmri;


create
    or replace function query_collections_by_quality_from(from1 real, q_id int) RETURNS table (
        id integer,
        name varchar(255),
        institution_id varchar(255),
        number_of_records integer,
        added_by integer,
        person_name varchar(255)
        ) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, quality_characteristic q, quality_characteristic_collection qmc, person p
WHERE c.id=qmc.collection_id and qmc.quality_characteristic_id=q.id
  and p.id = c.added_by
  and qmc.quality_characteristic_value_for_collection >= from1
  and q.id=q_id
$$ language sql;

alter function query_collections_by_quality_from(from1 real, q_id int) owner to bbmri;


create
    or replace function query_collections_by_quality_to(to1 real, q_id int) RETURNS table (
      id integer,
      name varchar(255),
      institution_id varchar(255),
      number_of_records integer,
      added_by integer,
      person_name varchar(255)
  ) as $$
select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, quality_characteristic q, quality_characteristic_collection qmc, person p
WHERE c.id=qmc.collection_id and qmc.quality_characteristic_id=q.id
  and p.id = c.added_by
  and qmc.quality_characteristic_value_for_collection <= to1
  and q.id=q_id
$$ language sql;

alter function query_collections_by_quality_to(to1 real, q_id int) owner to bbmri;


select distinct c.id, c.name, c.institution_id, c.number_of_records,
                c.added_by, (p.first_name || ' ' || p.last_name)::varchar(255)
FROM collection c, quality_characteristic q, quality_characteristic_collection qmc, person p
WHERE c.id=qmc.collection_id and qmc.quality_characteristic_id=q.id
  and p.id = c.added_by
  and qmc.quality_characteristic_value_for_collection between 0.6 and 1
  and q.id=56;

select * from query_collections_by_quality(1,1,'57')
