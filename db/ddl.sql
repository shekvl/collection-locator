-- POSTGRES SQL statements to CREATE all necessary TABLES for the collection locator.
-- This tables reference OMOP CDM vocabulary tables, which the collection locator is build upon.

create table address (
    address_id serial PRIMARY KEY,
    town varchar (255) NOT NULL,
    street_name varchar (255),
    house_number varchar (255),
    zip varchar (255) NOT NULL,
    country_code varchar (3) NOT NULL,
    UNIQUE (
        town,
        street_name,
        house_number,
        zip,
        country_code
    )
);

create table person (
    id serial PRIMARY KEY,
    first_name varchar (255) NOT NULL,
    last_name varchar (255) NOT NULL,
    email varchar (255),
    phone varchar (255),
    address_id integer REFERENCES address ON DELETE CASCADE ON UPDATE CASCADE,
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (first_name, last_name, email),
    UNIQUE (first_name, last_name, phone),
    UNIQUE (first_name, last_name, address_id)
);

-- biobanks that are members of BBMRI-ERIC have a dedicated bbmri_eric_id
create table institution (
    name varchar (255) PRIMARY KEY,
    bbmri_eric_id varchar (255),
    is_biobank boolean,
    website_url varchar (255),
    email varchar (255),
    phone varchar (255),
    address_id integer REFERENCES address ON DELETE CASCADE ON UPDATE CASCADE,
    added_by integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    last_modified timestamp with time zone NOT NULL DEFAULT NOW()
);

create table collection (
    id serial PRIMARY KEY,
    name varchar (255) NOT NULL,
    institution_id varchar (255) REFERENCES institution ON DELETE CASCADE ON UPDATE CASCADE,
    number_of_records integer NOT NULL CHECK (number_of_records > 0),
    completeness real CHECK (
        0 <= completeness
        AND completeness <= 1
    ),
    accuracy real CHECK (
        0 <= accuracy
        AND accuracy <= 1
    ),
    reliability real CHECK (
        0 <= reliability
        AND reliability <= 1
    ),
    timeliness real CHECK (
        0 <= timeliness
        AND timeliness <= 1
    ),
    consistancy real CHECK (
        0 <= consistancy
        AND consistancy <= 1
    ),
    added_by integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (name, institution_id, number_of_records)
);

drop table quality_characteristic;
create table quality_characteristic
(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    friendly_name varchar(255),
    description varchar(2500),
    added_by integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (name)
);

drop table quality_metric;
create table quality_metric
(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    friendly_name varchar(255),
    description varchar(2500),
    is_default boolean default false,
    metric_level varchar(35),
    quality_characteristic_id integer REFERENCES quality_characteristic ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    added_by integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (name)
);

drop table quality_characteristic_collection;
create table quality_characteristic_collection
(
    quality_characteristic_id integer REFERENCES quality_characteristic ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    collection_id integer REFERENCES collection ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    quality_characteristic_value_for_collection real CHECK (
        0 <= quality_characteristic_value_for_collection
        AND quality_characteristic_value_for_collection <= 1
    ),
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (quality_characteristic_id, collection_id)
);

drop table quality_metric_collection;
create table quality_metric_collection
(
    quality_metric_id integer REFERENCES quality_metric ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    collection_id integer REFERENCES collection ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    quality_metric_value_for_collection real CHECK (
                0 <= quality_metric_value_for_collection
            AND quality_metric_value_for_collection <= 1
        ),
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (quality_metric_id, collection_id)
);

drop table quality_characteristic_attribute;
create table quality_characteristic_attribute
(
    quality_characteristic_id integer REFERENCES quality_characteristic ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    attribute_id integer REFERENCES collection ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    quality_characteristic_value_for_attribute real CHECK (
        0 <= quality_characteristic_value_for_attribute
        AND quality_characteristic_value_for_attribute <= 1
    ),
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (quality_characteristic_id, attribute_id)
);

drop table quality_metric_attribute;
create table quality_metric_attribute
(
    quality_metric_id integer REFERENCES quality_metric ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    attribute_id integer REFERENCES collection ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    quality_metric_value_for_attribute real CHECK (
                0 <= quality_metric_value_for_attribute
            AND quality_metric_value_for_attribute <= 1
        ),
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (quality_metric_id, attribute_id)
);

-- contains attributes of the stored collections
create table attribute (
    id bigserial PRIMARY KEY,
    collection_id integer REFERENCES collection ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    attribute_name varchar (255) NOT NULL,
    completeness real CHECK (
        0 <= completeness
        AND completeness <= 1
    ),
    accuracy real CHECK (
        0 <= accuracy
        AND accuracy <= 1
    ),
    reliability real CHECK (
        0 <= reliability
        AND reliability <= 1
    ),
    timeliness real CHECK (
        0 <= timeliness
        AND timeliness <= 1
    ),
    consistancy real CHECK (
        0 <= consistancy
        AND consistancy <= 1
    ),
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (collection_id, attribute_name)
);

-- contains all the cdm concepts currently used by annotations within the collection locator
create table concept (
    code varchar (50) NOT NULL,
    vocabulary_id varchar (20) REFERENCES cdm.vocabulary ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    concept_id integer REFERENCES cdm.concept ON DELETE CASCADE ON UPDATE CASCADE,
    reference_count integer,
    PRIMARY KEY (code, vocabulary_id)
);

-- contains the concept annotations for each attribute (allows multiple annotations per attribute)
create table attribute_concept (
    attribute_id BIGINT REFERENCES attribute ON DELETE CASCADE ON UPDATE CASCADE,
    code varchar (50) NOT NULL,
    vocabulary_id varchar (20), -- REFERENCES cdm.vocabulary ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY(attribute_id, code, vocabulary_id)
);

create table person_institution (
    person_id integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE,
    institution_name varchar (255) REFERENCES institution ON DELETE CASCADE ON UPDATE CASCADE,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, institution_name)
);

-- contains sets of selected concept relationships for relationship search (e.g. LOINC axes)
create table relationship_of_interest (
    id SERIAL PRIMARY KEY,
    set varchar(255),
    name varchar(255),
    relationship_concept_id integer,
    relationship_id varchar(20) REFERENCES cdm.relationship ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    distinct_values varchar [],
    distinct_value_count bigint,
    vocabulary_id varchar(20) REFERENCES cdm.vocabulary ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    UNIQUE (set, name)
);

delete from quality_metric;
delete from quality_characteristic;
