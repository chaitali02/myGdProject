
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

