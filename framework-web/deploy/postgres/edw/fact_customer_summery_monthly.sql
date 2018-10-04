CREATE TABLE edw_small.fact_customer_summary_monthly
(
  customer_id VARCHAR(50) NOT NULL,
  yyyy_mm VARCHAR(50) NOT NULL,
  total_trans_count VARCHAR(50),
  total_trans_amount_usd integer,
  avg_trans_amount integer,
  min_amount numeric(38,2),
  max_amount numeric(38,2),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT fact_customer_summary_monthly_pkey PRIMARY KEY (customer_id, yyyy_mm, load_date,load_id)
);


