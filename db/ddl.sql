-- This will CREATE the necessary TABLES to manage USERS, COLLECTIONS and METADATA QUALITY extending the OMOP CDM.
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
    last_modified timestamp with time zone NOT NULL DEFAULT NOW()
    -- ,
    -- UNIQUE (collection_id, attribute_name)
);

create table concept (
    code varchar (50) NOT NULL,
    vocabulary_id varchar (20) REFERENCES cdm.vocabulary ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    concept_id integer REFERENCES cdm.concept ON DELETE CASCADE ON UPDATE CASCADE,
    reference_count integer,
    PRIMARY KEY (code, vocabulary_id)
);

create table attribute_concept (
    attribute_id BIGINT REFERENCES attribute ON DELETE CASCADE ON UPDATE CASCADE,
    code varchar (50) NOT NULL,
    vocabulary_id varchar (20) REFERENCES cdm.vocabulary ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY(attribute_id, code, vocabulary_id)
);

create table person_institution (
    person_id integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE,
    institution_name varchar (255) REFERENCES institution ON DELETE CASCADE ON UPDATE CASCADE,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, institution_name)
);

create table password (
    person_id integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE,
    password_hash varchar (255),
    active boolean NOT NULL DEFAULT (true),
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, password_hash)
);

create table role (
    id serial PRIMARY KEY,
    role varchar(255) UNIQUE NOT NULL
);

create table person_role (
    person_id integer REFERENCES person ON DELETE CASCADE ON UPDATE CASCADE,
    role_id integer REFERENCES role ON DELETE CASCADE ON UPDATE CASCADE,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, role_id)
);

create table session (
    sid varchar (36) PRIMARY KEY,
    expires timestamp with time zone NOT NULL,
    data text NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT NOW(),
    last_modified timestamp with time zone NOT NULL DEFAULT NOW()
);

create table relationship_of_interest (
    id SERIAL PRIMARY KEY,
    "group" varchar(255),
    name varchar(255),
    relationship_concept_id integer,
    relationship_id varchar(20) REFERENCES cdm.relationship ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    distinct_values varchar [],
    distinct_value_count bigint,
    vocabulary_id varchar(20) REFERENCES cdm.vocabulary ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    last_modified timestamp with time zone NOT NULL DEFAULT NOW()
);