CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table industry_factor_transpose set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS industry_factor_transpose;

CREATE TABLE IF NOT EXISTS `industry_factor_transpose`(
  `iteration_id` int, 
  `reporting_date` string,
  `factor` string, 
  `factor_value` double,
  `version` string);
 
