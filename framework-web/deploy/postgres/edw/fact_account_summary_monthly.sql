DROP TABLE fact_account_summary_monthly;

CREATE TABLE fact_account_summary_monthly
(
  account_id text NOT NULL,
  yyyy_mm text NOT NULL,
  total_trans_count integer,
  total_trans_amount_usd integer,
  avg_trans_amount integer,
  min_amount numeric(38,2),
  max_amount integer,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT fact_account_summary_monthly_pkey PRIMARY KEY (account_id, yyyy_mm, load_date)
);

ALTER TABLE fact_account_summary_monthly OWNER TO inferyx;









