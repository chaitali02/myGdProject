drop table if exists product_type;
create table product_type(	
	product_type_id varchar(50) default 0 not null,
	product_type_code varchar(10),
	product_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint product_type_id_pk primary key(product_type_id));
