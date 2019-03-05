drop table if exists dim_branch;
create table dim_branch(	
	branch_id varchar(50) default 0 not null,
	src_branch_id varchar(50),
	branch_type_code varchar(10),
	branch_name varchar(100),
	branch_desc varchar(500),
	branch_contact_name varchar(100),
	branch_contact_phone varchar(100),
	branch_contact_email varchar(100),
	load_date varchar(10),
	load_id integer(50), 
constraint branch_id_pk  primary key(branch_id));