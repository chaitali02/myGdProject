drop table if exists dim_transaction_type;
create table dim_transaction_type(	
	transaction_type_id varchar(50) default 0 not null,
	src_transaction_type_id varchar(50),
	transaction_type_code varchar(10),
	transaction_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint load_id_pk  primary key(transaction_type_id));
