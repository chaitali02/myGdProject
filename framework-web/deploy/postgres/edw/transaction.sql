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




