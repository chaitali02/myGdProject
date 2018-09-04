CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS customer;
CREATE TABLE IF NOT EXISTS `customer`(
  `customer_id` string, 
  `address_id` string, 
  `branch_id` int, 
  `title` string, 
  `first_name` string, 
  `middle_name` string, 
  `last_name` string, 
  `ssn` string, 
  `phone` string, 
  `date_first_purchase` string, 
  `commute_distance_miles` int)
  PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_medium.customer ADD PARTITION (load_date='2017-07-01', load_id='00');
