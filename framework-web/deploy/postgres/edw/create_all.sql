  
CREATE TABLE edw_small.account
(
  account_id VARCHAR(50) NOT NULL,
  account_type_id integer,
  account_status_id integer,
  product_type_id integer,
  customer_id VARCHAR(50),
  pin_number integer,
  nationality VARCHAR(50),
  primary_iden_doc VARCHAR(50),
  primary_iden_doc_id VARCHAR(50),
  secondary_iden_doc VARCHAR(50),
  secondary_iden_doc_id VARCHAR(50),
  account_open_date VARCHAR(50),
  account_number VARCHAR(50),
  opening_balance VARCHAR(50),
  current_balance VARCHAR(50),
  overdue_balance integer,
  overdue_date VARCHAR(50),
  currency_code VARCHAR(50),
  interest_type VARCHAR(50),
  interest_rate double precision,
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT account_pkey PRIMARY KEY (account_id,load_date,load_id)
);


CREATE TABLE edw_small.account_status_type
(
  account_status_id integer NOT NULL,
  account_status_code VARCHAR(50),
  account_status_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT account_status_type_pkey PRIMARY KEY (account_status_id, load_date,load_id)
);

CREATE TABLE edw_small.account_type
(
  account_type_id integer NOT NULL,
  account_type_code VARCHAR(50),
  account_type_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT account_type_pkey PRIMARY KEY (account_type_id,load_date,load_id)
);

CREATE TABLE edw_small.address
(
  address_id VARCHAR(50) NOT NULL,
  address_line1 VARCHAR(50),
  address_line2 VARCHAR(50),
  address_line3 VARCHAR(50),
  city VARCHAR(50),
  county VARCHAR(50),
  state VARCHAR(50),
  zipcode integer,
  country VARCHAR(50),
  latitude VARCHAR(50),
  longitude VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT address_pkey PRIMARY KEY (address_id, load_date,load_id)
);



CREATE TABLE edw_small.bank
(
  bank_id integer NOT NULL,
  bank_code VARCHAR(50),
  bank_name VARCHAR(50),
  bank_account_number VARCHAR(50),
  bank_currency_code VARCHAR(50),
  bank_check_digits integer,
  load_date VARCHAR(50),
  load_id integer ,
  CONSTRAINT dim_bank_pkey PRIMARY KEY (bank_id,load_date, load_id)
  
);


CREATE TABLE edw_small.branch
(
  branch_id integer NOT NULL,
  branch_type_id integer,
  bank_id VARCHAR(50),
  address_id VARCHAR(50),
  branch_name VARCHAR(50),
  branch_desc VARCHAR(50),
  branch_contact_name VARCHAR(50),
  branch_contact_phone VARCHAR(50),
  branch_contact_email VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT branch_pkey PRIMARY KEY (branch_id, load_date,load_id)
);


CREATE TABLE edw_small.branch_type
(
  branch_type_id integer NOT NULL,
  branch_type_code VARCHAR(50),
  branch_type_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT branch_type_pkey PRIMARY KEY (branch_type_id, load_date,load_id)
);


CREATE TABLE edw_small.customer
(
  customer_id VARCHAR(50) NOT NULL,
  address_id VARCHAR(50),
  branch_id integer,
  title VARCHAR(50),
  first_name VARCHAR(50),
  middle_name VARCHAR(50),
  last_name VARCHAR(50),
  ssn VARCHAR(50),
  phone VARCHAR(50),
  date_first_purchase VARCHAR(50),
  commute_distance_miles integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT customer_pkey PRIMARY KEY (customer_id,load_date,load_id)
);



CREATE TABLE edw_small.dim_account
(
  account_id VARCHAR(50) NOT NULL,
  src_account_id VARCHAR(50),
  account_type_code VARCHAR(50),
  account_status_code VARCHAR(50),
  product_type_code VARCHAR(50),
  pin_number integer,
  nationality VARCHAR(50),
  primary_iden_doc VARCHAR(50),
  primary_iden_doc_id VARCHAR(50),
  secondary_iden_doc VARCHAR(50),
  secondary_iden_doc_id VARCHAR(50),
  account_open_date VARCHAR(50),
  account_number VARCHAR(50),
  opening_balance VARCHAR(50),
  current_balance VARCHAR(50),
  overdue_balance integer,
  overdue_date VARCHAR(50),
  currency_code VARCHAR(50),
  interest_type VARCHAR(50),
  interest_rate numeric(38,2),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_account_pkey PRIMARY KEY (account_id, load_date, load_id),
  CONSTRAINT src_account_id UNIQUE (src_account_id, load_date, load_id)
);

CREATE TABLE edw_small.dim_address
(
  address_id VARCHAR(50) NOT NULL,
  src_address_id VARCHAR(50),
  address_line1 VARCHAR(50),
  address_line2 VARCHAR(50),
  address_line3 VARCHAR(50),
  city VARCHAR(50),
  county VARCHAR(50),
  state VARCHAR(50),
  zipcode integer,
  country VARCHAR(50),
  latitude VARCHAR(50),
  longtitude VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_address_pkey PRIMARY KEY (address_id, load_date, load_id),
  CONSTRAINT src_address_id UNIQUE (src_address_id, load_date, load_id)
);


CREATE TABLE edw_small.dim_bank
(
  bank_id VARCHAR(50) NOT NULL,
  src_bank_id VARCHAR(50),
  bank_code VARCHAR(50),
  bank_name VARCHAR(50),
  bank_account_number VARCHAR(50),
  bank_currency_code VARCHAR(50),
  bank_check_digits integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_bank_ppkey PRIMARY KEY (bank_id, load_date, load_id),
  CONSTRAINT src_bank_id_uk UNIQUE (src_bank_id, load_date, load_id)
);

CREATE TABLE edw_small.dim_branch
(
  branch_id VARCHAR(50) NOT NULL,
  src_branch_id integer,
  branch_type_code VARCHAR(50),
  branch_name VARCHAR(50),
  branch_desc VARCHAR(50),
  branch_contact_name VARCHAR(50),
  branch_contact_phone VARCHAR(50),
  branch_contact_email VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_branch_pkey PRIMARY KEY (branch_id, load_date, load_id),
  CONSTRAINT src_branch_id UNIQUE (src_branch_id, load_date, load_id)
);


CREATE TABLE edw_small.dim_country(
  country_code VARCHAR(50) NOT NULL,
  country_name VARCHAR(50),
  country_population integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_country_pkey PRIMARY KEY (country_code,load_date, load_id)
  );


CREATE TABLE edw_small.dim_customer
(
  customer_id VARCHAR(50) NOT NULL,
  src_customer_id VARCHAR(50),
  title VARCHAR(50),
  first_name VARCHAR(50),
  middle_name VARCHAR(50),
  last_name VARCHAR(50),
  address_line1 VARCHAR(50),
  address_line2 VARCHAR(50),
  phone VARCHAR(50),
  date_first_purchase VARCHAR(50),
  commute_distance integer,
  city VARCHAR(50),
  state VARCHAR(50),
  postal_code VARCHAR(50),
  country VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_customer_pkey PRIMARY KEY (customer_id, load_date, load_id),
  CONSTRAINT src_customer_id UNIQUE (src_customer_id, load_date, load_id)
);


CREATE TABLE edw_small.dim_date
(
  date_id integer NOT NULL,
  date_type VARCHAR(50),
  date_val VARCHAR(50),
  day_num_of_week integer,
  day_num_of_month integer,
  day_num_of_quarter integer,
  day_num_of_year integer,
  day_num_absolute integer,
  day_of_week_name VARCHAR(50),
  day_of_week_abbreviation VARCHAR(50),
  julian_day_num_of_year integer,
  julian_day_num_absolute integer,
  is_weekday VARCHAR(50),
  is_usa_civil_holiday VARCHAR(50),
  is_last_day_of_week VARCHAR(50),
  is_last_day_of_month VARCHAR(50),
  is_last_day_of_quarter VARCHAR(50),
  is_last_day_of_year VARCHAR(50),
  is_last_day_of_fiscal_month VARCHAR(50),
  is_last_day_of_fiscal_quarter VARCHAR(50),
  is_last_day_of_fiscal_year VARCHAR(50),
  week_of_year_begin_date VARCHAR(50),
  week_of_year_begin_date_key integer,
  week_of_year_end_date VARCHAR(50),
  week_of_year_end_date_key integer,
  week_of_month_begin_date VARCHAR(50),
  week_of_month_begin_date_key integer,
  week_of_month_end_date VARCHAR(50),
  week_of_month_end_date_key integer,
  week_of_quarter_begin_date VARCHAR(50),
  week_of_quarter_begin_date_key integer,
  week_of_quarter_end_date VARCHAR(50),
  week_of_quarter_end_date_key integer,
  week_num_of_month integer,
  week_num_of_quarter integer,
  week_num_of_year integer,
  month_num_of_year integer,
  month_num_overall VARCHAR(50),
  month_name VARCHAR(50),
  month_name_abbreviation VARCHAR(50),
  month_begin_date VARCHAR(50),
  month_begin_date_key integer,
  month_end_date VARCHAR(50),
  month_end_date_key integer,
  quarter_num_of_year integer,
  quarter_num_overall integer,
  quarter_begin_date VARCHAR(50),
  quarter_begin_date_key integer,
  quarter_end_date VARCHAR(50),
  quarter_end_date_key integer,
  year_num integer,
  year_begin_date VARCHAR(50),
  year_begin_date_key integer,
  year_end_date VARCHAR(50),
  year_end_date_key integer,
  yyyy_mm VARCHAR(50),
  yyyy_mm_dd VARCHAR(50),
  dd_mon_yyyy VARCHAR(50),
  load_date VARCHAR(50),
  load_id integer,
  CONSTRAINT dim_date_pkey PRIMARY KEY (date_id),
  CONSTRAINT date_val UNIQUE (date_val, load_date)
);

CREATE TABLE edw_small.dim_state(
  state_code VARCHAR(50),
  state_name VARCHAR(50),
  country_code VARCHAR(50), 
  state_population integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_state_pkey PRIMARY KEY (state_code,load_date,load_id)
  );




CREATE TABLE edw_small.dim_transaction_type
(
  transaction_type_id VARCHAR(50) NOT NULL,
  src_transaction_type_id VARCHAR(50),
  transaction_type_code VARCHAR(50),
  transaction_type_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_transaction_type_pkey PRIMARY KEY (transaction_type_id, load_date, load_id),
  CONSTRAINT src_transaction_type_id UNIQUE (src_transaction_type_id, load_date, load_id)

);

CREATE TABLE edw_small.dp_rule_results
(
  datapoduuid VARCHAR(50),
  datapodversion VARCHAR(50),
  datapodname VARCHAR(50),
  attributeid VARCHAR(50),
  attributename VARCHAR(50),
  numrows VARCHAR(50),
  minval double precision,
  maxval double precision,
  avgval double precision,
  medianval double precision,
  stddev double precision,
  numdistinct integer,
  perdistinct double precision,
  numnull integer,
  pernull double precision,
  sixsigma double precision,
  load_date VARCHAR(50),
  load_id integer,
  version integer
);


CREATE TABLE edw_small.dq_rule_results
(
  rowkey VARCHAR(50),
  datapoduuid VARCHAR(50),
  datapodversion VARCHAR(50),
  datapodname VARCHAR(50),
  attributeid VARCHAR(50),
  attributename VARCHAR(50),
  attributevalue VARCHAR(50),
  nullcheck_pass VARCHAR(50),
  valuecheck_pass VARCHAR(50),
  rangecheck_pass VARCHAR(50),
  datatypecheck_pass VARCHAR(50),
  dataformatcheck_pass VARCHAR(50),
  lengthcheck_pass VARCHAR(50),
  refintegritycheck_pass VARCHAR(50),
  dupcheck_pass VARCHAR(50),
  customcheck_pass VARCHAR(50),
  version integer
);


CREATE TABLE edw_small.fact_account_summary_monthly
(
  account_id VARCHAR(50) NOT NULL,
  yyyy_mm VARCHAR(50) NOT NULL,
  total_trans_count integer,
  total_trans_amount_usd integer,
  avg_trans_amount integer,
  min_amount numeric(38,2),
  max_amount integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT fact_account_summary_monthly_pkey PRIMARY KEY (account_id, yyyy_mm, load_date,load_id)
);


CREATE TABLE edw_small.fact_customer_summary_monthly
(
  customer_id VARCHAR(50) NOT NULL,
  yyyy_mm VARCHAR(50) NOT NULL,
  total_trans_count VARCHAR(50),
  total_trans_amount_usd integer,
  avg_trans_amount integer,
  min_amount numeric(38,2),
  max_amount numeric(38,2),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT fact_customer_summary_monthly_pkey PRIMARY KEY (customer_id, yyyy_mm, load_date,load_id)
);


CREATE TABLE edw_small.fact_transaction
(
  transaction_id VARCHAR(50),
  src_transaction_id VARCHAR(50),
  transaction_type_id VARCHAR(50),
  trans_date_id integer,
  bank_id VARCHAR(50),
  branch_id VARCHAR(50),
  customer_id VARCHAR(50),
  address_id VARCHAR(50),
  account_id VARCHAR(50),
  from_account VARCHAR(50),
  to_account VARCHAR(50),
  amount_base_curr integer,
  amount_usd integer,
  currency_code VARCHAR(50),
  currency_rate integer,
  notes VARCHAR(50),
  load_date VARCHAR(50),
  load_id integer
);


CREATE TABLE edw_small.model_training_set
(
  customer_id integer NOT NULL,
  address_id integer NOT NULL,
  branch_id integer NOT NULL,
  commute_distance_miles integer NOT NULL,
  label integer NOT NULL,
  censor integer NOT NULL,
  version integer NOT NULL
);


CREATE TABLE edw_small.product_type
(
  product_type_id integer,
  product_type_code VARCHAR(50),
  product_type_desc VARCHAR(50),
  load_date VARCHAR(50),
  load_id integer,
  CONSTRAINT product_type_pkey PRIMARY KEY (product_type_id, load_date, load_id)

);


CREATE TABLE edw_small.rc_rule_results (
    sourcedatapoduuid VARCHAR(50),
    sourcedatapodversion VARCHAR(50),
    sourcedatapodname VARCHAR(50),
    sourceattributeid VARCHAR(50),
    sourceattributename VARCHAR(50),
    sourcevalue double precision,
    targetdatapoduuid VARCHAR(50),
    targetdatapodversion VARCHAR(50),
    targetdatapodname VARCHAR(50),
    targetattributeid VARCHAR(50),
    targetattributename VARCHAR(50),
    targetvalue double precision,
    status VARCHAR(50),
    version integer
);


CREATE TABLE edw_small.target_gen_data_uniform_dist (
  id integer NOT NULL,
  col1 double precision,
  version integer NOT NULL
);

CREATE TABLE edw_small.target_sim_linear_regression (
  id integer NOT NULL,
  version integer NOT NULL,
  interest_rate double precision,
  account_type_id double precision,
  account_status_id double precision
);


CREATE TABLE edw_small.target_sim_multivarient_normal_dis (
  id integer NOT NULL,
  interestRate double precision,
  col2 double precision,
  col3 double precision,
  version integer NOT NULL  
);



CREATE TABLE edw_small.transaction
(
  transaction_id VARCHAR(50),
  transaction_type_id VARCHAR(50),
  account_id VARCHAR(50),
  transaction_date VARCHAR(50),
  from_account VARCHAR(50),
  to_account VARCHAR(50),
  amount_base_curr numeric(30,2),
  amount_usd numeric(30,2),
  currency_code VARCHAR(50),
  currency_rate double precision,
  notes VARCHAR(100),
  load_date VARCHAR(50),
  load_id integer,
  CONSTRAINT transaction_pkey PRIMARY KEY (transaction_id, load_date, load_id)
);

CREATE TABLE edw_small.transaction_type
(
  transaction_type_id VARCHAR(50),
  transaction_type_code VARCHAR(50),
  transaction_type_desc VARCHAR(50),
  load_date VARCHAR(50),
  load_id integer,
  CONSTRAINT transaction_type_pkey PRIMARY KEY (transaction_type_id, load_date, load_id)
);








