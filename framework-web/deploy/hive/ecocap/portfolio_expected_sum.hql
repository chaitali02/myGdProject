CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_expected_sum;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_expected_sum`(
  `expected_sum` decimal,
  `reporting_date` string,
  `version` int);
  

