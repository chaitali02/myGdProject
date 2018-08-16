CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS industry_factor_simulation;
CREATE  TABLE IF NOT EXISTS `industry_factor_simulation`(
  `iteration_id` int, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `reporting_date` string,
  `version` int);