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

