CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS dim_state;
CREATE TABLE IF NOT EXISTS `dim_state`(
  `state_code` string,
  `state_name` string,
  `country_code` string, 
  `state_population` int,
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE edw_medium.dim_state ADD PARTITION(load_date='2017-12-04', load_id='00');
