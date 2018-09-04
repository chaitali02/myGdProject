DROP TABLE IF EXISTS industry_factor_correlation_transpose;
CREATE  TABLE IF NOT EXISTS `industry_factor_correlation_transpose`(
  `factor_x` string, 
  `reporting_date` string, 
  `factor_y` string, 
  `factor_value` double, 
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';