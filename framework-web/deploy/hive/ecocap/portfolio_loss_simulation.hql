CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_loss_simulation;
CREATE  TABLE IF NOT EXISTS `portfolio_loss_simulation`(
  `iterationid` int, 
  `portfolio_loss` bigint, 
  `reporting_date` string,
  `version` int);