DROP TABLE IF EXISTS dim_transaction_type;

CREATE TABLE dim_transaction_type
(
  transaction_type_id text NOT NULL,
  src_transaction_type_id text,
  transaction_type_code text,
  transaction_type_desc text,
  load_date text NOT NULL,
  load_id integer NOT NULL
);

ALTER TABLE dim_transaction_type OWNER TO inferyx;

