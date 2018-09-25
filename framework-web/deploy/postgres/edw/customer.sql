DROP TABLE customer;  

CREATE TABLE customer
(
  customer_id text NOT NULL,
  address_id text,
  branch_id integer,
  title text,
  first_name text,
  middle_name text,
  last_name text,
  ssn text,
  phone text,
  date_first_purchase text,
  commute_distance_miles integer,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT customer_pkey PRIMARY KEY (customer_id, load_date)
);

ALTER TABLE customer OWNER TO inferyx;
