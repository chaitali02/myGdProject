DROP TABLE IF EXISTS dim_customer;

CREATE TABLE dim_customer
(
  customer_id text NOT NULL,
  src_customer_id text,
  title text,
  first_name text,
  middle_name text,
  last_name text,
  address_line1 text,
  address_line2 text,
  phone text,
  date_first_purchase text,
  commute_distance integer,
  city text,
  state text,
  postal_code text,
  country text,
  load_date text NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_customer_pkey PRIMARY KEY (customer_id, load_date, load_id),
  CONSTRAINT src_customer_id UNIQUE (src_customer_id, load_date, load_id)
);

ALTER TABLE dim_customer OWNER TO inferyx;
