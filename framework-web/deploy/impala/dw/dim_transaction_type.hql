CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS dim_transaction_type;
CREATE  TABLE IF NOT EXISTS `dim_transaction_type`(
  `transaction_type_id` string,
  `src_transaction_type_id` int,
  `transaction_type_code` string, 
  `transaction_type_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE framework.dim_transaction_type ADD PARTITION(load_date='2017-07-01', load_id=00);
