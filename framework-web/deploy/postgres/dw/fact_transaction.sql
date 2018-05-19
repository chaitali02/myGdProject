



-- Table: framework.fact_transaction

-- DROP TABLE framework.fact_transaction;

CREATE TABLE framework.fact_transaction
(
  transaction_id integer,
  src_transaction_id text,
  transaction_type_id integer,
  trans_date_id integer,
  bank_id integer,
  branch_id integer,
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
)
WITH (
  OIDS=FALSE
);
ALTER TABLE framework.fact_transaction
  OWNER TO inferyx;


