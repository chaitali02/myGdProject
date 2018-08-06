CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_loss_aggr;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_loss_aggr`(
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal,
  `reporting_date` string,
  `version` int);

