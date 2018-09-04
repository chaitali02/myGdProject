CREATE DATABASE IF NOT EXISTS edw_small ;
use edw_small ;
DROP TABLE IF EXISTS address;
CREATE EXTERNAL TABLE IF NOT EXISTS `address`(
  `address_id` string, 
  `address_line1` string, 
  `address_line2` string, 
  `address_line3` string, 
  `city` string, 
  `county` string, 
  `state` string, 
  `zipcode` int, 
  `country` string, 
  `latitude` string, 
  `longitude` string)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_small .address ADD PARTITION (load_date='0000-00-00', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/edw_small /upload/address_small.csv' OVERWRITE INTO TABLE edw_small .address PARTITION (load_date='2017-07-01', load_id='00');  

