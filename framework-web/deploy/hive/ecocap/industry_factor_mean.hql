DROP TABLE IF EXISTS industry_factor_mean;
CREATE TABLE IF NOT EXISTS `industry_factor_mean`(
  `id` string, 
  `mean` double, 
  `reporting_date` string,
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';