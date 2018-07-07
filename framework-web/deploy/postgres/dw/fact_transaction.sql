DROP TABLE framework.fact_transaction;

CREATE TABLE framework.fact_transaction
(
  transaction_id text,
  src_transaction_id text,
  transaction_type_id text,
  trans_date_id integer,
  bank_id text,
  branch_id text,
  customer_id text,
  address_id text,
  account_id text,
  from_account text,
  to_account text,
  amount_base_curr integer,
  amount_usd integer,
  currency_code text,
  currency_rate integer,
  notes text,
  load_date text,
  load_id integer
);

ALTER TABLE framework.fact_transaction OWNER TO inferyx;
