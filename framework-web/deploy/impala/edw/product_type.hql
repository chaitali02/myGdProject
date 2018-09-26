CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS product_type;
CREATE EXTERNAL TABLE IF NOT EXISTS `product_type`(
  `product_type_id` int, 
  `product_type_code` string, 
  `product_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.product_type ADD PARTITION (load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/product_type.csv' OVERWRITE INTO TABLE framework.product_type PARTITION (load_date='2017-07-01', load_id='00'); 
