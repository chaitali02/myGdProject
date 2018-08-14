CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
alter table industry_factor_correlation_transpose set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS industry_factor_correlation_transpose;


CREATE  TABLE IF NOT EXISTS `industry_factor_correlation_transpose`(
  `factor_x` string, 
  `reporting_date` string, 
  `factor_y` string, 
  `factor_value` double, 
  `version` int);



