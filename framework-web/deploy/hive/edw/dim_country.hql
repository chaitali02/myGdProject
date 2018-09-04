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