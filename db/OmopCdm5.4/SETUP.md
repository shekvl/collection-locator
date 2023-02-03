# Introduction

To set up the OMOP CDM, you first need to
1. create an instance of the `OMOP CDM` and then
2. populate it with a selection of OHDSI `vocabularies`.

> For this guid, we are using version 5.4 of the OMOP CDM.

# OMOP CDM

## Download

> This step is only necessary, if you need to download a CDM version which is not yet present in this repo.

Download DDLs from GitHub ([OMOP CDM v5.4](https://github.com/OHDSI/CommonDataModel/tree/v5.4.0/inst/ddl/5.4/postgresql)) to create a OMOP CDM schema.

For each file:
1. Click on File
2. Select `Raw` View Mode
3. Right Click to `save page as..`
4. Save File to `./db/OmopCdm5.4`

> The download should included the following 4 files:
> - OMOPCDM_postgresql_5.4_ddl.sql
> - OMOPCDM_postgresql_5.4_primary_keys.sql
> - OMOPCDM_postgresql_5.4_indices.sql
> - OMOPCDM_postgresql_5.4_constraints.sql


## Create Postgres DB Schema

Login to your Postgres DBMS:

```bash
psql -U postgres
```
Connect to your database (psql terminal):

```psql
\c collection_locator
```

Create schema for specific OMOP CDM version:

```sql
create schema cdm;
```

## Prepare Downloaded Files

> This step is only necessary, if you downloaded a CDM version which is not yet present in this repo.

The downloaded DDLs will have a `@cdmDatabaseSchema` schema prefix that is not compatible with Postgres an thus will throw and error.

We can simply replace this prefix with our own schema prefix `omop_cdm_5.4`. The `sed` bash command lets us replace all matches in a file:

```bash
sed -i s/search_string/replace_string/ file_name
```

Replace schema prefix (bash terminal):

```bash
sed -i s/@cdmDatabaseSchema./\"omop_cdm_5.4\"./ ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_ddl.sql
sed -i s/@cdmDatabaseSchema./\"omop_cdm_5.4\"./ ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_primary_keys.sql
sed -i s/@cdmDatabaseSchema./\"omop_cdm_5.4\"./ ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_indices.sql
sed -i s/@cdmDatabaseSchema./\"omop_cdm_5.4\"./ ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_constraints.sql
```

Make sure the files have been modified successfully:

```bash
head -n 20 ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_ddl.sql
```

## Execute OMOP CDM DDLs

The OMOP CDM can now be created by executing the DDLs. We can use `\include`  to execute .sql file in the psql terminal as if the contained SQL statements were run directly in the terminal.

```psql
\include ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_ddl.sql;
\include ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_primary_keys.sql;
\include ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_indices.sql;
```

Make sure you also executed the last command.

`OMOPCDM_postgresql_5.4_constraints.sql` should be `executed later`, after the vocabulary tables have been populated with the downloaded vocabulary data (see below).


# Vocabularies

## Download

> This step is only necessary, if you want to have a different subset or version of vocabularies than currently present in this repository.

Login to [Athena](https://athena.ohdsi.org) and download Vocabularies for populating the OMOP CDM:

1. Login
2. Go to `DOWNLOAD` tab
3. Select Vocabularies
4. Click on `DOWNLOAD VOCABULARIES`

> The selected vocabularies will be bundled into a .zip file, that can be downloaded.

5. View Vocabulary Archives in `DOWNLOAD HISTORY`
6. Download Archive once Download is ready
7. Unzip Archive to a Separate Folder in `./db/AthenaVocabDownloads`

> The .zip file should contain the following files:
> - CONCEPT.csv
> - CONCEPT_SYNONYM.csv
> - CONCEPT_ANCESTOR.csv
> - CONCEPT_CLASS.csv
> - CONCEPT_RELATIONSHIP.csv
> - VOCABULARY.csv
> - DOMAIN.csv
> - RELATIONSHIP.csv
> - DRUG_STRENGTH.csv

> The vocabulary part of the OMOP CDM schema contains all concepts and relationships of each vocabulary. Only this part of the schema is used by the Collection-Locator.

## Prepare Downloaded Files

The two downloaded .csv files `CONCEPT` and `CONCEPT_SYNONYMS` do not comply to standard CSV format (due to problematic quotes).

Therefore, we need to delete the first line (heading) of this two file, so we can import the data via `text mode` to our database.

```bash
sed -i 1d ./db/AthenaVocabDownloads/LOINC+/CONCEPT.csv
sed -i 1d ./db/AthenaVocabDownloads/LOINC+/CONCEPT_SYNONYM.csv
```

Check if it worked:

```bash
head -n 5 ./db/AthenaVocabDownloads/LOINC+/CONCEPT.csv
```

## Populate OMOP CDM Vocabulary Tables

To load data into the vocabulary tables, we can use the [`\copy`](https://www.postgresql.org/docs/current/sql-copy.html) psql command.

```psql
\copy table_name from file_name [options]
```
> E... to tell terminal to expect escaped symboles as delimiter<br>
> CSV... csv mode

Files complying to the CSV format can be imported via `CSV mode`:

```psql
\copy cdm.drug_strength from ./db/AthenaVocabDownloads/LOINC+/DRUG_STRENGTH.csv DELIMITER E'\t' CSV HEADER;
\copy cdm.vocabulary from ./db/AthenaVocabDownloads/LOINC+/VOCABULARY.csv DELIMITER E'\t' CSV HEADER;
\copy cdm.relationship from ./db/AthenaVocabDownloads/LOINC+/RELATIONSHIP.csv DELIMITER E'\t' CSV HEADER;
\copy cdm.concept_ancestor from ./db/AthenaVocabDownloads/LOINC+/CONCEPT_ANCESTOR.csv DELIMITER E'\t' CSV HEADER;
\copy cdm.concept_class from ./db/AthenaVocabDownloads/LOINC+/CONCEPT_CLASS.csv DELIMITER E'\t' CSV HEADER;
\copy cdm.concept_relationship from ./db/AthenaVocabDownloads/LOINC+/CONCEPT_RELATIONSHIP.csv DELIMITER E'\t' CSV HEADER;
\copy cdm.domain from ./db/AthenaVocabDownloads/LOINC+/DOMAIN.csv DELIMITER E'\t' CSV HEADER;
```

Other files have to be imported via `text mode`:

```psql
\copy cdm.concept from ./db/AthenaVocabDownloads/LOINC+/CONCEPT.csv DELIMITER E'\t';
\copy cdm.concept_synonym from ./db/AthenaVocabDownloads/LOINC+/CONCEPT_SYNONYM.csv DELIMITER E'\t';
```

Foreign key constraints have to be established `after` the vocabulary tables have been populated (previous step). Otherwise, we would get the message: `"update violates foreign key constraint"`

Execute foreign key constrains:

```psql
\include ./db/OmopCdm5.4/OMOPCDM_postgresql_5.4_constraints.sql;
```


# Test query

To check whether the OMOP CDM is now functional, run the folloing query:

```sql
select *
from cdm.concept
where concept_id in (45884349, 45884274, 44818516);
```
