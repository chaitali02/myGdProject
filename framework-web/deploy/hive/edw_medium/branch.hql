CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium ;
DROP TABLE IF EXISTS branch;
CREATE TABLE IF NOT EXISTS `branch`(
  `branch_id` string, 
  `branch_type_id` string, 
  `bank_id` string, 
  `address_id` int, 
  `branch_name` string, 
  `branch_desc` string, 
  `branch_contact_name` string, 
  `branch_contact_phone` string, 
  `branch_contact_email` string)
  PARTITIONED BY (
  `load_date` string,
  `load_id` BIGINT)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_medium.branch ADD PARTITION (load_date='2017-07-01', load_id='00');
