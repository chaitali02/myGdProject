drop table if exists dim_country;
create table dim_country(	
	country_code varchar(10),
	country_name varchar(100),
	country_population integer(10), 
    country_risk_level integer(50),
	load_date varchar(10),
	load_id integer(50));
