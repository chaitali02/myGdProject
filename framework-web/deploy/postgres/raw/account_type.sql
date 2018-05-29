DROP TABLE framework.account_type;

CREATE TABLE framework.account_type
(
  account_type_id integer NOT NULL,
  account_type_code text,
  account_type_desc text,
  load_date text NOT NULL,
  version integer,
  load_id integer,
  CONSTRAINT account_type_pkey PRIMARY KEY (account_type_id, load_date)
);

Copy framework.account_type(account_type_id,account_type_code,account_type_desc,load_date)FROM '/user/hive/warehouse/framework/upload/account_type.csv' delimiter ',' csv header;
