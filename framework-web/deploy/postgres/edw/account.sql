DROP TABLE account;
   
CREATE TABLE account
(
  account_id text NOT NULL,
  account_type_id integer,
  account_status_id integer,
  product_type_id integer,
  customer_id text,
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
  interest_rate double precision,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT account_pkey PRIMARY KEY (account_id, load_date)
);
ALTER TABLE account OWNER TO inferyx;

