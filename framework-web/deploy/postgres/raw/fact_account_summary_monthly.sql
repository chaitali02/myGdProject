
CREATE TABLE framework.fact_account_summary_monthly (
  account_id text ,
  yyyy_mm text ,
  total_trans_count integer ,
  total_trans_amount_usd integer ,
  avg_trans_amount integer ,
  min_amount decimal(38,2) ,
  max_amount integer ,
  load_date text ,
  load_id integer,
  PRIMARY KEY (account_id,yyyy_mm,load_date)
);
