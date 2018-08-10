CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
alter table portfolio_expected_sum set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS portfolio_expected_sum;
CREATE  TABLE IF NOT EXISTS `portfolio_expected_sum`(
  `expected_sum` decimal,
  `reporting_date` string,
  `version` int);

 