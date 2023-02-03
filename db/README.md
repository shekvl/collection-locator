## Overview

The postgres database consists of two schemas: the `cdm schema` containing the OMOP CDM tables and the `public schema` containing the tables to manage the repository of collection annotations.

The [OmopCdm5.4](./OmopCdm5.4) directory contains the OMOP CDM version 5.4 ready to be executed.

The [AthenaVocabDownloads/LOINC+](./AthenaVocabDownloads) directory contains a selection of OHDSI Vocabularies including SNOMED and LOINC which can be used to populate the CDM.

## Database Setup

1. Create the database `collection-locator`
2. Set up an instance of the `OMOP CDM` => [setup guid](./OmopCdm5.4/SETUP.md)
3. Enter the following commands into your database.

    ```psql
    \include ./db/ddl.sql;
    \include ./db/CRUD.sql;
    \include ./db/triggers.sql;
    \include ./db/functions.sql;
    \include ./db/datasets.sql;
    \include ./db/views.sql;
    \include ./db/records.sql;
    ```
    These commands execute SQL statments that create `tables, constraints, triggers, views, functions and table functions` that make up the `collection locator repository`.

    `records.sql` contains initial records to make the locator work right away.

    All other SQL files contain statements that will update the respective table/trigger/view etc. or drop it prior to re-execution, so that changes can directly be applied by re-running the SQL file.
