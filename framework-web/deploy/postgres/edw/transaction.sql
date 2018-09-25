DROP TABLE transaction;

CREATE TABLE transaction
(
  transaction_id text,
  transaction_type_id text,
  account_id text,
  transaction_date text,
  from_account text,
  to_account text,
  amount_base_curr numeric(30,2),
  amount_usd numeric(30,2),
  currency_code text,
  currency_rate double precision,
  notes text,
  load_date text,
  load_id integer
);




