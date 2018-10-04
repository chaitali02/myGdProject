CREATE TABLE edw_small.product_type
(
  product_type_id integer,
  product_type_code VARCHAR(50),
  product_type_desc VARCHAR(50),
  load_date VARCHAR(50),
  load_id integer,
  CONSTRAINT product_type_pkey PRIMARY KEY (product_type_id, load_date, load_id)
);

