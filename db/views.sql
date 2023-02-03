-- Currently, all cdm vocabularies are shown which goes beyond well known standard ontologies. TODO: Replace by actual table with subset of cdm.vocabulary? Would make updates necessary whenever the set of selected vocabularies changes.
-- view supported vocabularies
create or replace view supported_vocabulary
as
    select * from cdm.vocabulary;


-- view attributes and their annotated concepts
create or replace view annotation
as
	select a.collection_id, a.id as attribute_id, a.attribute_name,
	ac.code, ac.vocabulary_id, a.completeness, a.accuracy, a.reliability, a.timeliness,
	a.consistancy from attribute a, attribute_concept ac where a.id = ac.attribute_id