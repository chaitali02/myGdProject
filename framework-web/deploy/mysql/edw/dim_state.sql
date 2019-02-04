drop table if exists dim_state;
create table dim_state(	
	state_id varchar(50) default 0 not null,
	state_code varchar(10),
	state_name varchar(100),
	country_code varchar(10),
	state_population integer(10),
	load_date varchar(10),
	load_id integer(50), 
constraint state_id_pk  primary key(state_id));