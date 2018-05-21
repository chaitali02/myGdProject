
-- Table: framework.account

   DROP TABLE framework.account;
   
CREATE TABLE framework.account (
  account_id text ,
  account_type_id integer ,
  account_status_id integer ,
  product_type_id integer ,
  customer_id text ,
  pin_number integer ,
  nationality text ,
  primary_iden_doc text ,
  primary_iden_doc_id text ,
  secondary_iden_doc text ,
  secondary_iden_doc_id text ,
  account_open_date text ,
  account_number text ,
  opening_balance text ,
  current_balance text ,
  overdue_balance integer ,
  overdue_date text ,
  currency_code text ,
  interest_type text ,
  interest_rate float ,
  load_date text ,
  version text ,
  load_id integer,
  PRIMARY KEY (account_id,load_date)
);
\copy framework.account(account_id,account_type_id,account_status_id,product_type_id,customer_id,pin_number,nationality,primary_iden_doc,primary_iden_doc_id,secondary_iden_doc,secondary_iden_doc_id,account_open_date,account_number,opening_balance,current_balance,overdue_balance,overdue_date,currency_code,interest_type,interest_rate,load_date) FROM /home/inferyx/account.csv delimiter ',' csv skip header;
