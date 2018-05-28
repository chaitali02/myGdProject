DROP TABLE framework.bank;

CREATE TABLE framework.bank
(
  bank_id text,
  bank_code text,
  bank_name text,
  bank_account_number text,
  bank_currency_code text,
  bank_check_digits integer,
  load_date text,
  load_id integer
);
  
Copy framework.bank(bank_id,bank_code,bank_name,bank_account_number,bank_currency_code,bank_check_digits,load_date)FROM '/user/hive/warehouse/framework/upload/bank.csv' delimiter ',' csv header;
  
