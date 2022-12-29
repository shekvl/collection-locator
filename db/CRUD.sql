
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


--TODO enable insert without optional values

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
-- optional parameters default to null, leaving attribute unchanged
-- returning updated row
CREATE OR REPLACE FUNCTION update_record_collection (collection_id integer, name varchar(255) default null, institution_id varchar(255) default null, number_of_records integer default null, completeness real default null, accuracy real default null, reliability real default null, timeliness real default null, consistancy real default null, added_by integer default null)
RETURNS setof collection AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	IF name is not null
	THEN
		update collection c set name = name where c.id = collection_id;
	END IF;
	IF institution_id is not null
	THEN
		update collection c set institution_id = institution_id where c.id = collection_id;
	END IF;
	IF number_of_records is not null
	THEN
		update collection c set number_of_records = number_of_records where c.id = collection_id;
	END IF;
	IF completeness is not null
	THEN
		update collection c set completeness = completeness where c.id = collection_id;
	END IF;
	IF accuracy is not null
	THEN
		update collection c set accuracy = accuracy where c.id = collection_id;
	END IF;
	IF reliability is not null
	THEN
		update collection c set reliability = reliability where c.id = collection_id;
	END IF;
	IF timeliness is not null
	THEN
		update collection c set timeliness = timeliness where c.id = collection_id;
	END IF;
	IF consistancy is not null
	THEN
		update collection c set consistancy = consistancy where c.id = collection_id;
	END IF;
	IF added_by is not null
	THEN
		update collection c set added_by = added_by where c.id = collection_id;
	END IF;
	return query
		select * from collection c where c.id = collection_id;
END;
$$ LANGUAGE plpgsql;


-- insert record into address table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_address (town varchar(255), street_name varchar(255), house_number varchar(255), zip varchar(255), country_code varchar(3))
RETURNS setof address AS $$
	insert into address (town, street_name, house_number, zip, country_code)
	values(town, street_name, house_number, zip, country_code)
	returning *;
$$ LANGUAGE sql;


-- delete record from address table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_address (address_id int)
RETURNS integer AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	delete from address a
	where a.address_id = address_id;
	get diagnostics row_count = row_count;
	return row_count;
END;
$$ LANGUAGE plpgsql;


-- update record of address table
-- update any subset of attributes with specified id
-- update_record_address(id, attribute => value, ..)
-- optional parameters default to null, leaving attribute unchanged
-- returning updated row
CREATE OR REPLACE FUNCTION update_record_address (address_id integer, town varchar(255) default null, street_name varchar(255) default null, house_number varchar(255) default null, zip varchar(255) default null, country_code integer default null)
RETURNS setof address AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	IF town is not null
	THEN
		update address a set town = town where a.address_id = address_id;
	END IF;
	IF street_name is not null
	THEN
		update address a set street_name = street_name where a.address_id = address_id;
	END IF;
	IF house_number is not null
	THEN
		update address a set house_number = house_number where a.address_id = address_id;
	END IF;
	IF zip is not null
	THEN
		update address a set zip = zip where a.address_id = address_id;
	END IF;
	IF country_code is not null
	THEN
		update address a set country_code = country_code where a.address_id = address_id;
	END IF;
	return query
		select * from address a where a.address_id = address_id;
END;
$$ LANGUAGE plpgsql;


-- insert record into person table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_person (first_name varchar(255), last_name varchar(255), email varchar(255), phone varchar(255), address_id integer)
RETURNS setof person AS $$
	insert into person (first_name, last_name, email, phone, address_id)
	values(first_name, last_name, email, phone, address_id)
	returning *;
$$ LANGUAGE sql;


-- delete record from person table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_person (person_id int)
RETURNS integer AS $$
DECLARE
	row_count integer;
BEGIN
	delete from person
	where id = person_id;
	get diagnostics row_count = row_count;
	return row_count;
END;
$$ LANGUAGE plpgsql;


-- update record of person table
-- update any subset of attributes with specified id
-- update_record_person(id, attribute => value, ..)
-- optional parameters default to null, leaving attribute unchanged
-- returning updated row
CREATE OR REPLACE FUNCTION update_record_person (person_id integer, first_name varchar(255) default null, last_name varchar(255) default null, email varchar(255) default null, phone varchar(255) default null, address_id integer default null)
RETURNS setof person AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	IF first_name is not null
	THEN
		update person p set first_name = first_name where p.id = person_id;
	END IF;
	IF last_name is not null
	THEN
		update person p set last_name = last_name where p.id = person_id;
	END IF;
	IF email is not null
	THEN
		update person p set email = email where p.id = person_id;
	END IF;
	IF phone is not null
	THEN
		update person p set phone = phone where p.id = person_id;
	END IF;
	IF address_id is not null
	THEN
		update person p set address_id = address_id where p.id = person_id;
	END IF;
	return query
		select * from person p where p.id = person_id;
END;
$$ LANGUAGE plpgsql;


-- insert record into session table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_session (sid varchar(255), expires timestamp with time zone, data text)
RETURNS setof session AS $$
	insert into session (sid, expires, data)
	values(sid, expires, data)
	returning *;
$$ LANGUAGE sql;


-- delete record from session table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_session (sid varchar(255))
RETURNS integer AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	delete from session s
	where s.sid = sid;
	get diagnostics row_count = row_count;
	return row_count;
END;
$$ LANGUAGE plpgsql;


-- insert record into role table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_role (role varchar(255))
RETURNS setof role AS $$
	insert into role (role)
	values (role)
	returning *;
$$ LANGUAGE sql;


-- delete record from role table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_role (id integer)
RETURNS integer AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	delete from role r
	where r.id = id;
	get diagnostics row_count = row_count;
	return row_count;
END;
$$ LANGUAGE plpgsql;


-- update record of role table
-- update any subset of attributes with specified id
-- update_record_role(id, attribute => value, ..)
-- optional parameters default to null, leaving attribute unchanged
-- returning updated row
CREATE OR REPLACE FUNCTION update_record_role (role_id integer, role varchar(255) default null)
RETURNS setof role AS $$
DECLARE
	row_count integer;
BEGIN
	IF role is not null
	THEN
		update role set role = role where id = role_id;
	END IF;
	return query
		select * from role where id = role_id;
END;
$$ LANGUAGE plpgsql;


-- insert record into person_role table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_person_role (person_id integer, role_id integer)
RETURNS setof person_role AS $$
	insert into person_role (person_id, role_id)
	values (person_id, role_id)
	returning *;
$$ LANGUAGE sql;


-- delete records from person_role table
-- person_id + role_id deletes specific association
-- person_id as only argument deletes all records with specified person_id
-- role_id as only argument deletes all records with specified role_id
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_person_role (person_id integer default null, role_id integer default null)
RETURNS integer AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	IF person_id is not null and role_id is not null
	THEN
		delete from person_role pr
		where pr.person_id = person_id
		and pr.role_id = role_id;
	ELSIF person_id is not null
	THEN
		delete from person_role pr
		where pr.person_id = person_id;
	ELSIF role_id is not null
	THEN
		delete from person_role pr
		where pr.role_id = role_id;
	END IF;
		get diagnostics row_count = row_count;
		return row_count;
END;
$$ LANGUAGE plpgsql;


-- insert record into institution table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_institution (name varchar(255), bbmri_eric_id varchar(255), is_biobank boolean, website_url varchar(255), email varchar(255), phone varchar(255), address_id integer, added_by integer)
RETURNS setof institution AS $$
	insert into institution (name, bbmri_eric_id, is_biobank, website_url, email, phone, address_id, added_by)
	values (name, bbmri_eric_id, is_biobank, website_url, email, phone, address_id, added_by)
	returning *;
$$ LANGUAGE sql;


-- delete record from institution table
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_institution (name varchar(255))
RETURNS integer AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	delete from institution i
	where i.name = name;
	get diagnostics row_count = row_count;
	return row_count;
END;
$$ LANGUAGE plpgsql;


-- update record of institution table
-- update any subset of attributes with specified id
-- update_record_institution (id, attribute => value, ..)
-- optional parameters default to null, leaving attribute unchanged
-- returning updated row
CREATE OR REPLACE FUNCTION update_record_institution (name varchar(255), bbmri_eric_id varchar(255) default null, is_biobank boolean default null, website_url varchar(255) default null, email varchar(255) default null, phone varchar(255) default null, address_id integer default null, added_by integer default null)
RETURNS setof institution AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	IF bbmri_eric_id is not null
	THEN
		update institution i set bbmri_eric_id = bbmri_eric_id where i.name = name;
	END IF;
	IF is_biobank is not null
	THEN
		update institution i set is_biobank = is_biobank where i.name = name;
	END IF;
	IF website_url is not null
	THEN
		update institution i set website_url = website_url where i.name = name;
	END IF;
	IF email is not null
	THEN
		update institution i set email = email where i.name = name;
	END IF;
	IF phone is not null
	THEN
		update institution i set phone = phone where i.name = name;
	END IF;
	IF address_id is not null
	THEN
		update institution i set address_id = address_id where i.name = name;
	END IF;
	IF added_by is not null
	THEN
		update institution i set added_by = added_by where i.name = name;
	END IF;
	return query
		select * from institution i where i.name = name;
END;
$$ LANGUAGE plpgsql;


-- insert record into person_institution table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_person_institution (person_id integer, institution_name varchar(255))
RETURNS setof person_institution AS $$
	insert into person_institution (person_id, institution_name)
	values (person_id, institution_name)
	returning *;
$$ LANGUAGE sql;


-- delete records from person_institution table
-- person_id + institution_name deletes specific association
-- person_id as only argument deletes all records with specified person_id
-- institution_name as only argument deletes all records with specified institution_name
-- returning deletion row_count
CREATE OR REPLACE FUNCTION delete_record_person_institution (person_id integer default null, institution_name varchar(255) default null)
RETURNS integer AS $$
#variable_conflict use_variable
DECLARE
	row_count integer;
BEGIN
	IF person_id is not null and institution_name is not null
	THEN
		delete from person_institution pi
		where pi.person_id = person_id
		and pi.institution_name = institution_name;
	ELSIF person_id is not null
	THEN
		delete from person_institution pi
		where pi.person_id = person_id;
	ELSIF institution_name is not null
	THEN
		delete from person_institution pi
		where pi.institution_name = institution_name;
	END IF;
		get diagnostics row_count = row_count;
		return row_count;
END;
$$ LANGUAGE plpgsql;


-- insert record into password table
-- returning inserted row
CREATE OR REPLACE FUNCTION insert_record_password (person_id integer, password_hash varchar(255))
RETURNS setof password AS $$
	insert into password (person_id, password_hash)
	values (person_id, password_hash)
	returning *;
$$ LANGUAGE sql;


-- inactivate current password (only current password should be active)
-- old active password is automatically inactivated through trigger, when new password is inserted
-- only the 3 most recent passwords per person are stored, older password get automatically deleted through trigger
-- returning effected row
CREATE OR REPLACE FUNCTION inactivate_password (person_id integer)
RETURNS setof password AS $$
	update password p set active = false
	where p.person_id = person_id
	and p.active = true
	returning *;
$$ LANGUAGE sql;