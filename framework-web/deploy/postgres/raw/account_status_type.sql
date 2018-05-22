
-- Table: framework.account_status_type

   DROP TABLE framework.account_status_type;
   
CREATE TABLE account_status_type (
  account_status_id integer,
  account_status_code text,
  account_status_desc text,
  load_date text,
  version text,
  load_id integer,
  PRIMARY KEY (account_status_id,load_date,load_id)
); 



Copy framework.account_status_type(account_status_id,account_status_code,account_status_desc,load_date,version,load_id)FROM '/user/hive/warehouse/framework/upload/account_status_type.csv' delimiter ',' csv  header;
