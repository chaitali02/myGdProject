DROP TABLE framework.branch;

CREATE TABLE framework.branch
(
  branch_id integer NOT NULL,
  branch_type_id integer,
  bank_id text,
  address_id text,
  branch_name text,
  branch_desc text,
  branch_contact_name text,
  branch_contact_phone text,
  branch_contact_email text,
  load_date text NOT NULL,
  version integer,
  load_id integer,
  CONSTRAINT branch_pkey PRIMARY KEY (branch_id, load_date)
);

Copy framework.branch(branch_id,branch_type_id,bank_id,address_id,branch_name,branch_desc,branch_contact_name,branch_contact_phone,branch_contact_email,load_date)FROM '/user/hive/warehouse/framework/upload/branch.csv' delimiter ',' csv  header;

