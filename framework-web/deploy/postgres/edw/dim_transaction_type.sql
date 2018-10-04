CREATE TABLE edw_small.dim_transaction_type
(
  transaction_type_id VARCHAR(50) NOT NULL,
  src_transaction_type_id VARCHAR(50),
  transaction_type_code VARCHAR(50),
  transaction_type_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_transaction_type_pkey PRIMARY KEY (transaction_type_id, load_date, load_id),
  CONSTRAINT src_transaction_type_id UNIQUE (src_transaction_type_id, load_date, load_id)

);
