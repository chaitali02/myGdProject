CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer`(
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
ALTER TABLE framework.customer ADD PARTITION (load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/customer.csv' OVERWRITE INTO TABLE framework.customer PARTITION (load_date='2017-07-01', load_id='00'); 
