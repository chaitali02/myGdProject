drop table if exists address;
create table address(	
	address_id varchar(50) default 0 not null,
	address_line1 varchar(50),
	address_line2 varchar(50),
	address_line3 varchar(50),
	city varchar(100),
	county varchar(100),
	state varchar(100),
	zipcode integer(10),
	country varchar(100),
	latitude varchar(50),
	longitude varchar(50),
	load_date varchar(10),
	load_id integer(50), 
constraint address_id_pk  primary key(address_id));