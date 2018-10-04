CREATE TABLE edw_small.transaction_type
(
  transaction_type_id VARCHAR(50),
  transaction_type_code VARCHAR(50),
  transaction_type_desc VARCHAR(50),
  load_date VARCHAR(50),
  load_id integer,
  CONSTRAINT transaction_type_pkey PRIMARY KEY (transaction_type_id, load_date, load_id)
);

