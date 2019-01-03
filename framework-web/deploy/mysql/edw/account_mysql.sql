
drop table if exists account_mysql;
create table account_mysql(	account_id varchar(50) default 0 not null,
	account_type_id varchar(50),
	account_status_id varchar(50),
	product_type_id varchar(50),
	customer_id varchar(50),
	pin_number integer(50),
	nationality varchar(50),
	primary_iden_doc varchar(50),
	primary_iden_doc_id varchar(50),
	secondary_iden_doc varchar(50),
	secondary_iden_doc_id varchar(50),
	account_open_date varchar(10),
	account_number varchar(50),
	opening_balance integer(20),
	current_balance integer(20),
	overdue_balance integer(20),
	overdue_date varchar(10),
	currency_code varchar(10),
	interest_type varchar(10),
	interest_rate decimal(10,2),
	load_date varchar(10),
	load_id integer(50), 
constraint account_mysql_pk  primary key(account_id));
