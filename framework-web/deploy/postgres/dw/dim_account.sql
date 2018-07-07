DROP TABLE framework.dim_account;

CREATE TABLE framework.dim_account
(
  account_id text NOT NULL,
  src_account_id text,
  account_type_code text,
  account_status_code text,
  product_type_code text,
  pin_number integer,
  nationality text,
  primary_iden_doc text,
  primary_iden_doc_id text,
  secondary_iden_doc text,
  secondary_iden_doc_id text,
  account_open_date text,
  account_number text,
  opening_balance text,
  current_balance text,
  overdue_balance integer,
  overdue_date text,
  currency_code text,
  interest_type text,
  interest_rate numeric(38,2),
  load_date text NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_account_pkey PRIMARY KEY (account_id, load_date, load_id),
  CONSTRAINT src_account_id UNIQUE (src_account_id, load_date, load_id)
);
ALTER TABLE framework.dim_account
  OWNER TO inferyx;


