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
