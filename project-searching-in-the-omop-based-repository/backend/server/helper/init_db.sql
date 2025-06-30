
CREATE TABLE public.speed_up_tables_5
(
    "table_name" character varying(200) NOT NULL,
    "conceptId"    integer NOT NULL,
    "Min"      numeric,
    "Max"      numeric,
	column_name varchar(255)
);

ALTER TABLE ONLY public.speed_up_tables_5
    ADD CONSTRAINT speed_up_tables_5_pkey PRIMARY KEY ("table_name", "conceptId");
--