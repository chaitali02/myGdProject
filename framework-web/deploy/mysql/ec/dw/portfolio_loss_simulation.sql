CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_loss_simulation;
CREATE TABLE IF NOT EXISTS `portfolio_loss_simulation`(
  `iterationid` int, 
  `portfolio_loss` decimal)
   
