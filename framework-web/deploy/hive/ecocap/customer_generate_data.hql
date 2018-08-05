CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_generate_data;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_generate_data`(
  `id` int, 
  `data` double,
  `version` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.customer_generate_data ADD PARTITION(load_date='2017-07-01', load_id='00');


