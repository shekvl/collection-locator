-- Vocabularies selected for Athena Download
-- Relies on intersection with Reference CDM 'omop_cdm_5.4_loinc_only'
-- create or replace view supported_vocabulary
-- as
--     select * from cdm.vocabulary where vocabulary_id not in ('Type Concept', 'None')
--     except
--     select * from "omop_cdm_5.4_loinc_only".vocabulary where vocabulary_id <> 'LOINC'

--TODO replace by actual table with vocabularies? Has to be updated when set of selected vocabularies changes

create or replace view supported_vocabulary
as
    select * from cdm.vocabulary


-- Combined view of attributes and their annotated concepts
create or replace view annotation
as
	select a.collection_id, a.id as attribute_id, a.attribute_name,
	ac.code, ac.vocabulary_id, a.completeness, a.accuracy, a.reliability, a.timeliness,
	a.consistancy from attribute a, attribute_concept ac where a.id = ac.attribute_id