CREATE TABLE edw_small.customer
(
  customer_id VARCHAR(50) NOT NULL,
  address_id VARCHAR(50),
  branch_id integer,
  title VARCHAR(50),
  first_name VARCHAR(50),
  middle_name VARCHAR(50),
  last_name VARCHAR(50),
  ssn VARCHAR(50),
  phone VARCHAR(50),
  date_first_purchase VARCHAR(50),
  commute_distance_miles integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT customer_pkey PRIMARY KEY (customer_id,load_date,load_id)
);


