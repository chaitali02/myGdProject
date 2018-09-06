DROP TABLE IF EXISTS account_type;
CREATE TABLE IF NOT EXISTS `account_type`(
  `account_type_id` string, 
  `account_type_code` string, 
  `account_type_desc` string)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';