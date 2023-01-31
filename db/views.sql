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