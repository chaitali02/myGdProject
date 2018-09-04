CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS dim_country;
CREATE TABLE IF NOT EXISTS `dim_country`(
  `country_code` string,
  `country_name` string,
  `country_population` int, 
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE edw_small.dim_country ADD PARTITION(load_date='2017-12-04', load_id='00');
