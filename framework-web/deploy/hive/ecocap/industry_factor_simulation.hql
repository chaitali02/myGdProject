CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table industry_factor_simulation set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS industry_factor_simulation;

CREATE  TABLE IF NOT EXISTS `industry_factor_simulation`(
  `iteration_id` int, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `reporting_date` string,
  `version` int);
