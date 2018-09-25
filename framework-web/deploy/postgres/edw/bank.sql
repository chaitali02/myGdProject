DROP TABLE bank;

CREATE TABLE bank
(
  bank_id integer NOT NULL,
  bank_code text,
  bank_name text,
  bank_account_number text,
  bank_currency_code text,
  bank_check_digits integer,
  load_date text,
  load_id integer DEFAULT 0
);

ALTER TABLE bank OWNER TO inferyx;
