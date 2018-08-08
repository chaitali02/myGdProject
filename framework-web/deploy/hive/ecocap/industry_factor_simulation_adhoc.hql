CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS industry_factor_simulation_adhoc;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_simulation_adhoc`(
  `iteration_id` int, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `version` int);




