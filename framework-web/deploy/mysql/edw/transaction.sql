drop table if exists transaction;
create table transaction(	
	transaction_id varchar(50) default 0 not null,
	transaction_type_id varchar(50),
	account_id varchar(50),
	transaction_date varchar(10),
	from_account varchar(50),
	to_account varchar(50),
	amount_base_curr decimal(10,2),
	amount_usd decimal(10,2),
	currency_code varchar(10),
	currency_rate decimal(10,2),
	notes varchar(100),
	load_date varchar(10),
	load_id integer(50), 
constraint transaction_id_pk  primary key(transaction_id));