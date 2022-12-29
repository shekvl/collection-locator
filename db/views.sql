-- Vocabularies select for Athena Download
-- Relies on Reference CDM 'omop_cdm_5.4_loinc_only'
create or replace view onthology
as
    select * from cdm.vocabulary where vocabulary_id not in ('Type Concept', 'None')
    except
    select * from "omop_cdm_5.4_loinc_only".vocabulary where vocabulary_id <> 'LOINC'


create or replace view
as