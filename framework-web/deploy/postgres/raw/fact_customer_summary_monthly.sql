
CREATE TABLE framework.fact_customer_summary_monthly (
  customer_id text ,
  yyyy_mm text ,
  total_trans_count text ,
  total_trans_amount_usd integer ,
  avg_trans_amount integer ,
  min_amount decimal(38,2) ,
  max_amount decimal(38,2) ,
  load_date text ,
  load_id integer,
  PRIMARY KEY (customer_id,yyyy_mm,load_date)
);



