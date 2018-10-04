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

