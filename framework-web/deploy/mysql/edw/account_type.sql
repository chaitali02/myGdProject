drop table if exists account_type;
create table account_type(	
	account_type_id varchar(50) default 0 not null,
	account_type_code varchar(10),
	account_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint account_type_id_pk  primary key(account_type_id));