DROP TABLE IF EXISTS dim_bank;

CREATE TABLE dim_bank
(
  bank_id text NOT NULL,
  src_bank_id text,
  bank_code text,
  bank_name text,
  bank_account_number text,
  bank_currency_code text,
  bank_check_digits integer,
  load_date text NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_bank_pkey PRIMARY KEY (bank_id, load_date, load_id),
  CONSTRAINT src_bank_id UNIQUE (src_bank_id, load_date, load_id)
);

ALTER TABLE framework.dim_bank OWNER TO inferyx;
