CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_loss_simulation_aggr;
CREATE TABLE IF NOT EXISTS `portfolio_loss_simulation_aggr`(
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal,
  `reporting_date` string,
  `version` int);