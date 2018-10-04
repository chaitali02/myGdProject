CREATE TABLE edw_small.branch_type
(
  branch_type_id integer NOT NULL,
  branch_type_code VARCHAR(50),
  branch_type_desc VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT branch_type_pkey PRIMARY KEY (branch_type_id, load_date,load_id)
);
