CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_loss_simulation_el;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_loss_simulation_el`(
  `iterationid` int, 
  `portfolio_loss` decimal, 
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal, 
  `reporting_date` string,
  `version` int);

