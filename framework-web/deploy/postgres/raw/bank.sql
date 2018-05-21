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

  
  
  \copy framework.bank(bank_id,bank_code,bank_name,bank_account_number,bank_currency_code,bank_check_digits,load_date,load_id)FROM /user/hive/warehouse/framework/upload/bank.csv delimiter ',' csv  header;
  