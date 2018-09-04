CREATE DATABASE IF NOT EXISTS edw_small ;
use edw_small ;
DROP TABLE IF EXISTS dim_address;
CREATE TABLE IF NOT EXISTS `dim_address`(
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
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE edw_small .dim_address ADD PARTITION(load_date='2017-12-04', load_id='00');

