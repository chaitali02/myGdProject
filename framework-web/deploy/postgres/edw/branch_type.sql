DROP TABLE framework.branch_type;

CREATE TABLE framework.branch_type
(
  branch_type_id integer NOT NULL,
  branch_type_code text,
  branch_type_desc text,
  load_date text NOT NULL,
  version integer,
  load_id integer,
  CONSTRAINT branch_type_pkey PRIMARY KEY (branch_type_id, load_date)
);

Copy framework.branch_type(branch_type_id,branch_type_code,branch_type_desc,load_date)FROM '/user/hive/warehouse/framework/upload/branch_type.csv' delimiter ',' csv  header;
