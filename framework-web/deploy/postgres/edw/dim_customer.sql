CREATE TABLE edw_small.dim_customer
(
  customer_id VARCHAR(50) NOT NULL,
  src_customer_id VARCHAR(50),
  title VARCHAR(50),
  first_name VARCHAR(50),
  middle_name VARCHAR(50),
  last_name VARCHAR(50),
  address_line1 VARCHAR(50),
  address_line2 VARCHAR(50),
  phone VARCHAR(50),
  date_first_purchase VARCHAR(50),
  commute_distance integer,
  city VARCHAR(50),
  state VARCHAR(50),
  postal_code VARCHAR(50),
  country VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_customer_pkey PRIMARY KEY (customer_id, load_date, load_id),
  CONSTRAINT src_customer_id UNIQUE (src_customer_id, load_date, load_id)
);

