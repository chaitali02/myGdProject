
-- Table: framework.dim_transaction_type

-- DROP TABLE framework.dim_transaction_type;

CREATE TABLE framework.dim_transaction_type (
    transaction_type_id text NOT NULL,
    src_transaction_type_id integer,
    transaction_type_code text,
    transaction_type_desc text,
    load_date text NOT NULL,
    load_id integer NOT NULL
)WITH (
  OIDS=FALSE
);
ALTER TABLE framework.dim_transaction_type
  OWNER TO inferyx;

