CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS industry_factor_transpose;
CREATE TABLE IF NOT EXISTS `industry_factor_transpose`(
  `iteration_id` int, 
  `reporting_date` string,
  `factor` string, 
  `factor_value` double,
  `version` string);