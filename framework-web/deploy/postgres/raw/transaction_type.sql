



-- Table: framework.transaction_type

   DROP TABLE framework.transaction_type;

CREATE TABLE framework.transaction_type (
  transaction_type_id text ,
  transaction_type_code text ,
  transaction_type_desc text ,
  load_date text ,
  version integer ,
  load_id integer 
);

  Copy framework.transaction_type(transaction_type_id,transaction_type_code,transaction_type_desc,load_date,version,load_id)FROM '/user/hive/warehouse/framework/upload/transaction_type.csv' delimiter ',' csv  header;





