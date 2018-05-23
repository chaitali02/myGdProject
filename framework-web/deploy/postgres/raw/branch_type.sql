DROP TABLE framework.branch_type;

CREATE TABLE framework.branch_type (
  branch_type_id text,
  branch_type_code text ,
  branch_type_desc text ,
  load_date text ,
  load_id integer DEFAULT 0,
  PRIMARY KEY (branch_type_id,load_date)
);

Copy framework.branch_type(branch_type_id,branch_type_code,branch_type_desc,load_date)FROM '/user/hive/warehouse/framework/upload/branch_type.csv' delimiter ',' csv  header;
