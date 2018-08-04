CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_loss_simulation_el;
CREATE TABLE IF NOT EXISTS `portfolio_loss_simulation_el`(
  `iterationid` int(11) DEFAULT NULL, 
  `portfolio_loss` decimal, 
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal)
   
