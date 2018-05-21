
-- Table: framework.branch_type

   DROP TABLE framework.branch_type;

CREATE TABLE framework.branch_type (
  branch_type_id integer,
  branch_type_code text ,
  branch_type_desc text ,
  load_date text ,
  version integer ,
  load_id integer,
  PRIMARY KEY (branch_type_id,load_date)
);
  Copy framework.branch_type(branch_type_id,branch_type_code,branch_type_desc,load_date,version,load_id)FROM '/user/hive/warehouse/framework/upload/branch_type.csv' delimiter ',' csv  header;
