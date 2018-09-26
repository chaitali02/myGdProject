CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS dim_address;
CREATE  TABLE IF NOT EXISTS `dim_address`(
  `address_id` string, 
  `src_address_id` string,
  `address_line1` string, 
  `address_line2` string, 
  `address_line3` string, 
  `city` string, 
  `county` string, 
  `state` string, 
  `zipcode` int, 
  `country` string, 
  `latitude` string, 
  `longtitude` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE framework.dim_address ADD PARTITION(load_date='2017-07-01', load_id=00);
