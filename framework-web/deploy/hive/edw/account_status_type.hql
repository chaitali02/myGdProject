DROP TABLE IF EXISTS account_status_type;
CREATE TABLE IF NOT EXISTS `account_status_type`(
  `account_status_id` string, 
  `account_status_code` string, 
  `account_status_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

  
