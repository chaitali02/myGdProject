CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS customer_var_contribution_topn_perc;
CREATE  TABLE IF NOT EXISTS `customer_var_contribution_topn_perc`(
  `reporting_date` string, 
  `top_n` string, 
  `var_contribution_perc` double,
  `version` string);

alter table customer_var_contribution_topn_perc set tblproperties('EXTERNAL'='FALSE');

