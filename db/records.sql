-- default person
select * from insert_record_person('super', 'admin', null, null, null);

-- default institution
select * from insert_record_institution('test biobank', null, true, null, null, null, null, 1);



-- Example: how to extract relationship and store it as part of a relationship of interest set
-- insert LOINC relationship 'scale' as part of the set 'axes' into the relationship_of_interest table
insert into relationship_of_interest(
	"set",
	"name",
	"relationship_concept_id",
	"relationship_id",
	"distinct_values",
	"distinct_value_count",
	"vocabulary_id"
)
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
;

insert into relationship_of_interest(
	"set",
	"name",
	"relationship_concept_id",
	"relationship_id",
	"distinct_values",
	"distinct_value_count",
	"vocabulary_id"
)
select
	'axes',
	'system',
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
	and r.relationship_id = 'Has system'
	group by r.relationship_id, r.relationship_concept_id
;

insert into relationship_of_interest(
	"set",
	"name",
	"relationship_concept_id",
	"relationship_id",
	"distinct_values",
	"distinct_value_count",
	"vocabulary_id"
)
select
	'axes',
	'time',
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
	and r.relationship_id = 'Has time aspect'
	group by r.relationship_id, r.relationship_concept_id
;

insert into relationship_of_interest(
	"set",
	"name",
	"relationship_concept_id",
	"relationship_id",
	"distinct_values",
	"distinct_value_count",
	"vocabulary_id"
)
select
	'axes',
	'property',
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
	and r.relationship_id = 'Has property'
	group by r.relationship_id, r.relationship_concept_id
;

insert into relationship_of_interest(
	"set",
	"name",
	"relationship_concept_id",
	"relationship_id",
	"distinct_values",
	"distinct_value_count",
	"vocabulary_id"
)
select
	'axes',
	'method',
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
	and r.relationship_id = 'Has method'
	group by r.relationship_id, r.relationship_concept_id
;

insert into relationship_of_interest(
	"set",
	"name",
	"relationship_concept_id",
	"relationship_id",
	"distinct_values",
	"distinct_value_count",
	"vocabulary_id"
)
select
	'axes',
	'component',
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
	and r.relationship_id = 'Has component'
	group by r.relationship_id, r.relationship_concept_id
;
