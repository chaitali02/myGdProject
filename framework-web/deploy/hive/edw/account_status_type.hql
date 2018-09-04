DROP TABLE IF EXISTS account_status_type;
CREATE TABLE IF NOT EXISTS `account_status_type`(
  `account_status_id` int, 
  `account_status_code` string, 
  `account_status_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

  
