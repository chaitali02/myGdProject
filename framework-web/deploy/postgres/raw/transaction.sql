


-- Table: framework.transaction

   DROP TABLE framework.transaction;

CREATE TABLE framework.transaction (
  transaction_id text ,
  transaction_type_id integer ,
  account_id text ,
  transaction_date text ,
  from_account text ,
  to_account text ,
  amount_base_curr decimal(30,2) ,
  amount_usd decimal(30,2) ,
  currency_code text ,
  currency_rate float ,
  notes text ,
  load_date text ,
  version integer ,
  load_id integer 
);



\copy framework.transaction(transaction_id,transaction_type_id,account_id,transaction_date,from_account,to_account,amount_base_curr,amount_usd,currency_code,currency_rate,notes,load_date)FROM /user/hive/warehouse/framework/upload/transaction.csv delimiter ',' csv  header;



