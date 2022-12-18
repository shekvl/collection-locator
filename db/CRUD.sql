
-- insert record into attribute_concept table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_attribute_concept (id bigint, code varchar(50), vocab varchar(20))
RETURNS setof attribute_concept AS $$
	insert into attribute_concept (attribute_id, code, vocabulary_id) values(id, code, vocab)
	returning *;
$$ LANGUAGE sql;


-- delete record from attribute_concept table
-- delete all concepts of an attribute: delete_record_attribute_concept (id)
-- delete specific concept of an attribute: delete_record_attribute_concept (id, code, vocab)
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_attribute_concept (id bigint, vocab_code varchar(50) default '%', vocab varchar(20) default '%')
RETURNS integer AS $$
DECLARE
	row_count integer;
BEGIN
		delete from attribute_concept
		where attribute_id = id
		and code like vocab_code
		and vocabulary_id like vocab;
		get diagnostics row_count = row_count;
		return row_count;
END;
$$ LANGUAGE plpgsql;


-- insert record into attribute table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_attribute (collection_id integer, attribute_name varchar(255), completeness real, accuracy real, reliability real, timeliness real, consistancy real)
RETURNS setof attribute AS $$
	insert into attribute (collection_id, attribute_name, completeness, accuracy, reliability, timeliness, consistancy)
	values(collection_id, attribute_name, completeness, accuracy, reliability, timeliness, consistancy)
	returning *;
$$ LANGUAGE sql;


-- delete record from attribute table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_attribute (attribute_id bigint)
RETURNS integer AS $$
DECLARE
	row_count integer;
BEGIN
		delete from attribute
		where id = attribute_id;
		get diagnostics row_count = row_count;
		return row_count;
END;
$$ LANGUAGE plpgsql;


-- insert record into collection table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_collection (collection_name varchar(255), institution_id varchar(255), number_of_records integer, completeness real, accuracy real, reliability real, timeliness real, consistancy real, added_by integer)
RETURNS setof collection AS $$
	insert into collection (name, institution_id, number_of_records, completeness, accuracy, reliability, timeliness, consistancy, added_by) values(collection_name, institution_id, number_of_records, completeness, accuracy, reliability, timeliness, consistancy, added_by)
	returning *;
$$ LANGUAGE sql;


-- delete record from collection table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_collection (collection_id integer)
RETURNS integer AS $$
DECLARE
	row_count integer;
BEGIN
		delete from collection
		where id = collection_id;
		get diagnostics row_count = row_count;
		return row_count;
END;
$$ LANGUAGE plpgsql;


-- update record of collection table
-- update any subset of attributes with specified id
-- update_record_collection(id, attribute => value, ..)
-- returning
CREATE OR REPLACE FUNCTION update_record_collection (collection_id integer, name varchar(255) default null, institution_id varchar(255) default null, number_of_records integer default null, completeness real default null, accuracy real default null, reliability real default null, timeliness real default null, consistancy real default null, added_by integer default null)
RETURNS setof collection AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
		IF name is not null
		THEN
			update collection set name = name where id = collection_id;
		END IF;
		IF institution_id is not null
		THEN
			update collection set institution_id = institution_id where id = collection_id;
		END IF;
		IF number_of_records is not null
		THEN
			update collection set number_of_records = number_of_records where id = collection_id;
		END IF;
		IF completeness is not null
		THEN
			update collection set completeness = completeness where id = collection_id;
		END IF;
		IF accuracy is not null
		THEN
			update collection set accuracy = accuracy where id = collection_id;
		END IF;
		IF reliability is not null
		THEN
			update collection set reliability = reliability where id = collection_id;
		END IF;
		IF timeliness is not null
		THEN
			update collection set timeliness = timeliness where id = collection_id;
		END IF;
		IF consistancy is not null
		THEN
			update collection set consistancy = consistancy where id = collection_id;
		END IF;
		IF added_by is not null
		THEN
			update collection set added_by = added_by where id = collection_id;
		END IF;
		return query
			select * from collection where id = collection_id;
END;
$$ LANGUAGE plpgsql;