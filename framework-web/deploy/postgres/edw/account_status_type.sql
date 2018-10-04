CREATE TABLE edw_small.account_status_type
(
  account_status_id integer NOT NULL,
  account_status_code VARCHAR(50),
  account_status_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT account_status_type_pkey PRIMARY KEY (account_status_id, load_date,load_id)
);



