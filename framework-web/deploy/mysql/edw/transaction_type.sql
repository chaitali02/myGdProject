drop table if exists transaction_type;
create table transaction_type(	
	transaction_type_id varchar(50) default 0 not null,
	transaction_type_code varchar(10),
	transaction_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint transaction_type_id_pk  primary key(transaction_type_id));