CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_loss_simulation;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_loss_simulation`(
  `cust_id` string, 
  `iterationid` int, 
  `customer_loss` decimal, 
  `version` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.customer_loss_simulation ADD PARTITION(load_date='2017-07-01', load_id='00');


