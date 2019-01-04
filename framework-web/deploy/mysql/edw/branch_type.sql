drop table if exists branch_type;
create table branch_type(	
	branch_type_id varchar(50) default 0 not null,
	branch_type_code varchar(10),
	branch_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint branch_type_id_pk  primary key(branch_type_id));