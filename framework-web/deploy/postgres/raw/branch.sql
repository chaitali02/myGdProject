
CREATE TABLE framework.branch (
  branch_id integer,
  branch_type_id integer ,
  bank_id text ,
  address_id text ,
  branch_name text ,
  branch_desc text ,
  branch_contact_name text ,
  branch_contact_phone text ,
  branch_contact_email text ,
  load_date text ,
  version integer ,
  load_id integer,
  PRIMARY KEY (branch_id,load_date)
);

