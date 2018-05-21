
-- Table: framework.branch

   DROP TABLE framework.branch;
   
   
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
  \copy framework.branch(branch_id,branch_type_id,bank_id,address_id,branch_name,branch_desc,branch_contact_name,branch_contact_phone,branch_contact_email,load_date,version,load_id)FROM /user/hive/warehouse/framework/upload/branch.csv delimiter ',' csv  header;

