CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS dim_transaction_type;
CREATE TABLE IF NOT EXISTS `dim_transaction_type`(
  `transaction_type_id` string,
  `src_transaction_type_id` string,
  `transaction_type_code` string, 
  `transaction_type_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE edw_medium.dim_transaction_type ADD PARTITION(load_date='2017-12-04', load_id='00');

