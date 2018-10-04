CREATE TABLE edw_small.account_type
(
  account_type_id integer NOT NULL,
  account_type_code VARCHAR(50),
  account_type_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT account_type_pkey PRIMARY KEY (account_type_id,load_date,load_id)
);

