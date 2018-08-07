CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_loss_aggr_es;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_loss_aggr_es`(
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal,
  `expected_sum` decimal,
  `reporting_date` string,
  `version` int);
  


