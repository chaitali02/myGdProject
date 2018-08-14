CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table portfolio_loss_simulation_el set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS portfolio_loss_simulation_el;

CREATE  TABLE IF NOT EXISTS `portfolio_loss_simulation_el`(
  `iterationid` int, 
  `portfolio_loss` decimal, 
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal, 
  `reporting_date` string,
  `version` int);
