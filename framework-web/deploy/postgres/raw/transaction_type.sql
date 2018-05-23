DROP TABLE framework.transaction_type;

CREATE TABLE framework.transaction_type (
  transaction_type_id text ,
  transaction_type_code text ,
  transaction_type_desc text ,
  load_date text ,
  load_id integer DEFAULT 0
);

Copy framework.transaction_type(transaction_type_id,transaction_type_code,transaction_type_desc,load_date)FROM '/user/hive/warehouse/framework/upload/transaction_type.csv' delimiter ',' csv  header;





