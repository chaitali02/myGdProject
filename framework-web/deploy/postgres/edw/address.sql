CREATE TABLE edw_small.address
(
  address_id VARCHAR(50) NOT NULL,
  address_line1 VARCHAR(50),
  address_line2 VARCHAR(50),
  address_line3 VARCHAR(50),
  city VARCHAR(50),
  county VARCHAR(50),
  state VARCHAR(50),
  zipcode integer,
  country VARCHAR(50),
  latitude VARCHAR(50),
  longitude VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT address_pkey PRIMARY KEY (address_id, load_date,load_id)
);

