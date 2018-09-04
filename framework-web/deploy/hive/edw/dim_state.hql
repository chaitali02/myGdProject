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