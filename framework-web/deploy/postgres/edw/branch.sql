CREATE TABLE edw_small.branch
(
  branch_id integer NOT NULL,
  branch_type_id integer,
  bank_id VARCHAR(50),
  address_id VARCHAR(50),
  branch_name VARCHAR(50),
  branch_desc VARCHAR(50),
  branch_contact_name VARCHAR(50),
  branch_contact_phone VARCHAR(50),
  branch_contact_email VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer,
  CONSTRAINT branch_pkey PRIMARY KEY (branch_id, load_date,load_id)
);


