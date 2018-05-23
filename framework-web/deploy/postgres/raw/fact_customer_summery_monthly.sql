

-- Table: framework.fact_customer_summary_monthly

   DROP TABLE framework.fact_customer_summary_monthly;

CREATE TABLE framework.fact_customer_summary_monthly
(
  customer_id text NOT NULL,
  yyyy_mm text NOT NULL,
  total_trans_count text,
  total_trans_amount_usd integer,
  avg_trans_amount integer,
  min_amount numeric(38,2),
  max_amount numeric(38,2),
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT fact_customer_summary_monthly_pkey PRIMARY KEY (customer_id, yyyy_mm, load_date)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE framework.fact_customer_summary_monthly
  OWNER TO inferyx;




