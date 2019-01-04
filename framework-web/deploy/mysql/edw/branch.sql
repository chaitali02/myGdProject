drop table if exists branch;
create table branch(	
	branch_id varchar(50) default 0 not null,
	branch_type_id varchar(50),
	bank_id varchar(50),
	address_id varchar(50),
	branch_name varchar(100),
	branch_desc varchar(500),
	branch_contact_name varchar(100),
	branch_contact_phone varchar(100),
	branch_contact_email varchar(100),
	load_date varchar(10),
	load_id integer(50), 
constraint branch_id_pk  primary key(branch_id));