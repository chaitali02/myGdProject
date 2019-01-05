drop table if exists bank;
create table bank(	
	bank_id varchar(50) default 0 not null,
	bank_code varchar(10),
	bank_name varchar(100),
	bank_account_number varchar(50),
	bank_currency_code varchar(10),
	bank_check_digits integer(10),
	load_date varchar(10),
	load_id integer(50), 
constraint bank_id_pk  primary key(bank_id));