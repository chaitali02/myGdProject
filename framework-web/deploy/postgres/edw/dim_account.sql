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

