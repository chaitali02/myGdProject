CREATE TABLE edw_small.dim_country(
  country_code VARCHAR(50) NOT NULL,
  country_name VARCHAR(50),
  country_population integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_country_pkey PRIMARY KEY (country_code,load_date, load_id)
  );

