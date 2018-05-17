

CREATE TABLE framework.branch_type (
  branch_type_id integer,
  branch_type_code text ,
  branch_type_desc text ,
  load_date text ,
  version integer ,
  load_id integer,
  PRIMARY KEY (branch_type_id,load_date)
);
