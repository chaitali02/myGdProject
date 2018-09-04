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