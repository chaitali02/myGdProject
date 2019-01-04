drop table if exists account_status_type;
create table account_status_type(	
	account_status_id varchar(50) default 0 not null,
	account_status_code varchar(10),
	account_status_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint account_status_id_pk  primary key(account_status_id));