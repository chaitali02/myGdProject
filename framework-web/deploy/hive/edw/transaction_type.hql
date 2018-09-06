DROP TABLE IF EXISTS transaction_type;
CREATE TABLE IF NOT EXISTS `transaction_type`(
  `transaction_type_id` string, 
  `transaction_type_code` string, 
  `transaction_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';