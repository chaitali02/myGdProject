CREATE TABLE edw_small.dim_branch
(
  branch_id VARCHAR(50) NOT NULL,
  src_branch_id integer,
  branch_type_code VARCHAR(50),
  branch_name VARCHAR(50),
  branch_desc VARCHAR(50),
  branch_contact_name VARCHAR(50),
  branch_contact_phone VARCHAR(50),
  branch_contact_email VARCHAR(50),
  load_date VARCHAR(50) NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_branch_pkey PRIMARY KEY (branch_id, load_date, load_id),
  CONSTRAINT src_branch_id UNIQUE (src_branch_id, load_date, load_id)
);

