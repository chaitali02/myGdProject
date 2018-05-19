-- Table: framework.bank

-- DROP TABLE framework.bank;

CREATE TABLE framework.bank
(
  bank_id text,
  bank_code text,
  bank_name text,
  bank_account_number text,
  bank_currency_code text,
  bank_check_digits text,
  load_date text,
  load_id text
)
WITH (
  OIDS=FALSE
);
ALTER TABLE framework.bank
  OWNER TO postgres;
