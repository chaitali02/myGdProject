
CREATE TABLE framework.account_type (
  account_type_id integer,
  account_type_code text ,
  account_type_desc text ,
  load_date text ,
  version integer ,
  load_id integer,
  PRIMARY KEY (account_type_id,load_date)
);
