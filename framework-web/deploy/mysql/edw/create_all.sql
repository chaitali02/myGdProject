

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


drop table if exists account;
create table account(	account_id varchar(50) default 0 not null,
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
constraint account_id_pk  primary key(account_id));

drop table if exists account_status_type;
create table account_status_type(	
	account_status_id varchar(50) default 0 not null,
	account_status_code varchar(10),
	account_status_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint account_status_id_pk  primary key(account_status_id));
drop table if exists account_type;
create table account_type(	
	account_type_id varchar(50) default 0 not null,
	account_type_code varchar(10),
	account_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint account_type_id_pk  primary key(account_type_id));
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
drop table if exists branch;
create table branch(	
	branch_id varchar(50) default 0 not null,
	branch_type_id varchar(50),
	bank_id varchar(50),
	address_id varchar(50),
	branch_name varchar(100),
	branch_desc varchar(500),
	branch_contact_name varchar(100),
	branch_contact_phone varchar(100),
	branch_contact_email varchar(100),
	load_date varchar(10),
	load_id integer(50), 
constraint branch_id_pk  primary key(branch_id));
drop table if exists branch_type;
create table branch_type(	
	branch_type_id varchar(50) default 0 not null,
	branch_type_code varchar(10),
	branch_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint branch_type_id_pk  primary key(branch_type_id));
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
drop table if exists dim_account;
create table dim_account(	
	account_id varchar(50) default 0 not null,
	src_account_id varchar(50),
	account_type_code varchar(10),
	account_status_code varchar(10),
	product_type_code varchar(10),
	pin_number integer(10),
	nationality varchar(100),
	primary_iden_doc varchar(100),
	primary_iden_doc_id varchar(50),
	secondary_iden_doc varchar(100),
	secondary_iden_doc_id varchar(50),
	account_open_date varchar(10),
	account_number varchar(50),
	opening_balance integer(20),
	current_balance integer(20),
	overdue_balance integer(20),
	overdue_date varchar(10),
	currency_code varchar(10),
	interest_type varchar(50),
	interest_rate decimal(10,2),
        customer_id varchar(50),
	load_date varchar(10),
	load_id integer(50), 
constraint account_id_pk primary key(account_id));

drop table if exists dim_address;
create table dim_address(	
	address_id varchar(50) default 0 not null,
	src_address_id varchar(50),
	address_line1 varchar(50),
	address_line2 varchar(50),
	address_line3 varchar(50),
	city varchar(100),
	county varchar(100),
	state varchar(100),
	zipcode integer(10),
	country varchar(100),
	latitude varchar(50),
	longtitude varchar(50),
	load_date varchar(10),
	load_id integer(50), 
constraint address_id_pk  primary key(address_id));
drop table if exists dim_bank;
create table dim_bank(	
	bank_id varchar(50) default 0 not null,
	src_bank_id varchar(50),
	bank_code varchar(10),
	bank_name varchar(100),
	bank_account_number varchar(50),
	bank_currency_code varchar(50),
	bank_check_digits integer(20),
	load_date varchar(10),
	load_id integer(50), 
constraint bank_id_pk  primary key(bank_id));
drop table if exists dim_branch;
create table dim_branch(	
	branch_id varchar(50) default 0 not null,
	src_branch_id varchar(50),
	branch_type_code varchar(10),
	branch_name varchar(100),
	branch_desc varchar(500),
	branch_contact_name varchar(100),
	branch_contact_phone varchar(100),
	branch_contact_email varchar(100),
	load_date varchar(10),
	load_id integer(50), 
constraint branch_id_pk  primary key(branch_id));
drop table if exists dim_country;
create table dim_country(	
	country_code varchar(10),
	country_name varchar(100),
	country_population integer(10), 
        country_risk_level integer(50),
	load_date varchar(10),
	load_id integer(50));

drop table if exists dim_customer;
create table dim_customer(	
	customer_id varchar(50) default 0 not null,
	src_customer_id varchar(50),
	title varchar(100),
	first_name varchar(100),
	middle_name varchar(100),
	last_name varchar(100),
	address_line1 varchar(50),
	address_line2 varchar(50),
	phone varchar(50),
	date_first_purchase varchar(10),
	commute_distance integer(10),
	city varchar(100),
	state varchar(100),
	postal_code varchar(10),
	country varchar(100),
	load_date varchar(10),
	load_id integer(50), 
constraint customer_id_pk  primary key(customer_id));
drop table if exists dim_date;
create table dim_date(	
	date_id varchar(50) default 0 not null,
	date_type varchar(45),
	date_val varchar(45),
	day_num_of_week integer(10),
	day_num_of_month integer(10),
	day_num_of_quarter integer(10),
	day_num_of_year integer(10),
	day_num_absolute integer(10),
	day_of_week_name varchar(100),
	day_of_week_abbreviation varchar(45),
	julian_day_num_of_year integer(10),
	julian_day_num_absolute integer(10),
	is_weekday varchar(50),
	is_usa_civil_holiday varchar(50),
	is_last_day_of_week varchar(50),
	is_last_day_of_month varchar(50),
	is_last_day_of_quarter varchar(50),
	is_last_day_of_year varchar(50),
	is_last_day_of_fiscal_month varchar(50),
	is_last_day_of_fiscal_quarter varchar(50),
	is_last_day_of_fiscal_year varchar(50),
	week_of_year_begin_date varchar(10),
	week_of_year_begin_date_key integer(10),
	week_of_year_end_date varchar(10),
	week_of_year_end_date_key integer(10),
	week_of_month_begin_date varchar(10),
	week_of_month_begin_date_key integer(10),
	week_of_month_end_date varchar(10),
	week_of_month_end_date_key integer(10),
	week_of_quarter_begin_date varchar(10),
	week_of_quarter_begin_date_key integer(10),
	week_of_quarter_end_date varchar(10),
	week_of_quarter_end_date_key integer(10),
	week_num_of_month integer(10),
	week_num_of_quarter integer(10),
	week_num_of_year integer(10),
	month_num_of_year integer(10),
	month_num_overall varchar(50),
	month_name varchar(100),
	month_name_abbreviation varchar(100),
	month_begin_date varchar(10),
	month_begin_date_key integer(10),
	month_end_date varchar(10),
	month_end_date_key integer(10),
	quarter_num_of_year integer(10),
	quarter_num_overall integer(10),
	quarter_begin_date varchar(10),
	quarter_begin_date_key integer(10),
	quarter_end_date varchar(10),
	quarter_end_date_key integer(10),
	year_num integer(10),
	year_begin_date varchar(10),
	year_begin_date_key integer(10),
	year_end_date varchar(10),
	year_end_date_key integer(10),
	yyyy_mm varchar(50),
	yyyy_mm_dd varchar(50),
	dd_mon_yyyy varchar(50),
	load_date varchar(10),
	load_id integer(50), 
constraint date_id_pk  primary key(date_id));
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
drop table if exists dim_transaction_type;
create table dim_transaction_type(	
	transaction_type_id varchar(50) default 0 not null,
	src_transaction_type_id varchar(50),
	transaction_type_code varchar(10),
	transaction_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint load_id_pk  primary key(transaction_type_id));

DROP TABLE IF EXISTS dp_result_summary; 

CREATE TABLE dp_result_summary 
  ( 
     rule_exec_uuid    VARCHAR(50), 
     rule_exec_version INT(10), 
     rule_exec_time    VARCHAR(100), 
     rule_uuid         VARCHAR(50), 
     rule_version      INT(10), 
     rule_name         VARCHAR(100), 
     datapoduuid       VARCHAR(50) , 
     datapodversion    VARCHAR(50), 
     datapodname       VARCHAR(100), 
     attributeid       VARCHAR(50), 
     attributename     VARCHAR(100), 
     numrows           VARCHAR(50), 
     minval            DECIMAL(20, 4), 
     maxval            DECIMAL(20, 4), 
     avgval            DECIMAL(20, 4), 
     medianval         DECIMAL(20, 4), 
     stddev            DECIMAL(20, 4), 
     numdistinct       INTEGER(10), 
     perdistinct       DECIMAL(20, 4), 
     numnull           INTEGER(10), 
     pernull           DECIMAL(20, 4), 
     minlength         DECIMAL(20, 4), 
     maxlength         DECIMAL(20, 4), 
     avglength         DECIMAL(20, 4), 
     numduplicates     DECIMAL(20, 4), 
     version           INTEGER(10) 
  ); 

drop table if exists fact_account_summary_monthly;
create table fact_account_summary_monthly(	
	account_id varchar(50) default 0 not null,
	yyyy_mm varchar(50) default 0 not null,
	total_trans_count integer(10),
	total_trans_amount_usd integer(10),
	avg_trans_amount integer(10),
	min_amount decimal(10,2),
	max_amount integer(10),
	load_date varchar(10),
	load_id integer(50), 
constraint account_id_pk  primary key(account_id,yyyy_mm));
drop table if exists fact_customer_summary_monthly;
create table fact_customer_summary_monthly(	
	customer_id varchar(50) default 0 not null,
	yyyy_mm varchar(50) default 0 not null,
	total_trans_count varchar(50),
	total_trans_amount_usd integer(10),
	avg_trans_amount integer(10),
	min_amount decimal(10,2),
	max_amount decimal(10,2),
	load_date varchar(10),
	load_id integer(50), 
constraint customer_id_pk  primary key(customer_id,yyyy_mm));
drop table if exists fact_transaction;
create table fact_transaction(	
	transaction_id varchar(50) default 0 not null,
	src_transaction_id varchar(50),
	transaction_type_id varchar(50),
	trans_date_id varchar(50),
	bank_id varchar(50),
	branch_id varchar(50),
	customer_id varchar(50),
	address_id varchar(50),
	account_id varchar(50),
	from_account varchar(50),
	to_account varchar(50),
	amount_base_curr integer(10),
	amount_usd integer(10),
	currency_code varchar(10),
	currency_rate integer(10),
	notes varchar(200),
	load_date varchar(10),
	load_id integer(50), 
constraint transaction_id_pk  primary key(transaction_id));

drop table if exists model_training_set;
create table model_training_set
(
  customer_id int(11) default null,
  address_id int(11) default null,
  branch_id int(11) default null,
  commute_distance_miles int(11) default null,
  label int(11) default null,
  censor int(11) default null,
  version int(11) default null
);
drop table if exists product_type;
create table product_type(	
	product_type_id varchar(50) default 0 not null,
	product_type_code varchar(10),
	product_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint product_type_id_pk primary key(product_type_id));

drop table if exists rc_rule_results;
create table rc_rule_results(	
	sourceuuid varchar(50) default 0 not null,
	sourceversion varchar(50),
	sourcename varchar(100),
	sourceattributeid varchar(50),
	sourceattributename varchar(100),
	sourcevalue decimal(10,2),
	targetuuid varchar(50) default 0 not null,
	targetversion varchar(50),
	targetname varchar(100),
	targetattributeid varchar(50),
	targetattributename varchar(100),
	targetvalue decimal(10,2),
	status varchar(50),
	version integer(10));

drop table if exists target_gen_data_uniform_dist;
create table target_gen_data_uniform_dist (
  id int(11) default null,
  col1 double precision,
  version int(11) default null
);
drop table if exists target_sim_linear_regression;
create table target_sim_linear_regression (
  id int(11) default null,
  version int(11) default null,
  interest_rate double precision,
  account_type_id double precision,
  account_status_id double precision
);
drop table if exists target_sim_multivarient_normal_dis;
create table target_sim_multivarient_normal_dis (
  id int(11) default null,
  interestrate double precision,
  col2 double precision,
  col3 double precision,
  version int(11) default null
);
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
drop table if exists transaction_type;
create table transaction_type(	
	transaction_type_id varchar(50) default 0 not null,
	transaction_type_code varchar(10),
	transaction_type_desc varchar(500),
	load_date varchar(10),
	load_id integer(50), 
constraint transaction_type_id_pk  primary key(transaction_type_id));