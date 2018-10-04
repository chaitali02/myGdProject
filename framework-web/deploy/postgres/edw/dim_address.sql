CREATE TABLE edw_small.dim_address
(
  address_id VARCHAR(50) NOT NULL,
  src_address_id VARCHAR(50),
  address_line1 VARCHAR(50),
  address_line2 VARCHAR(50),
  address_line3 VARCHAR(50),
  city VARCHAR(50),
  county VARCHAR(50),
  state VARCHAR(50),
  zipcode integer,
  country VARCHAR(50),
  latitude VARCHAR(50),
  longtitude VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_address_pkey PRIMARY KEY (address_id, load_date, load_id),
  CONSTRAINT src_address_id UNIQUE (src_address_id, load_date, load_id)
);

