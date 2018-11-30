drop table if exists customer;
create table customer(	
	customer_id varchar(50) default 0 not null,
	address_id varchar(50),
	branch_id varchar(50),
	title varchar(100),
	first_name varchar(100),
	middle_name varchar(100),
	last_name varchar(100),
	ssn varchar(100),
	phone varchar(100),
	date_first_purchase varchar(10),
	commute_distance_miles integer(10),
	load_date varchar(10),
	load_id integer(50), 
constraint customer_id_pk  primary key(customer_id));