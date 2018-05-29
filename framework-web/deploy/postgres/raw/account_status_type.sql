DROP TABLE framework.account_status_type;
   
CREATE TABLE framework.account_status_type
(
  account_status_id integer NOT NULL,
  account_status_code text,
  account_status_desc text,
  load_date text NOT NULL,
  version text,
  load_id integer,
  CONSTRAINT account_status_type_pkey PRIMARY KEY (account_status_id, load_date)
);

Copy framework.account_status_type(account_status_id,account_status_code,account_status_desc,load_date)FROM '/user/hive/warehouse/framework/upload/account_status_type.csv' delimiter ',' csv  header;
