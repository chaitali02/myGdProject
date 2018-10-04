CREATE TABLE edw_small.dim_state(
  state_code VARCHAR(50),
  state_name VARCHAR(50),
  country_code VARCHAR(50), 
  state_population integer,
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_state_pkey PRIMARY KEY (state_code,load_date,load_id)
  );


