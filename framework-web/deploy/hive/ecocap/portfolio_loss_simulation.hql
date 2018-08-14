CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table portfolio_loss_simulation set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS portfolio_loss_simulation;

CREATE  TABLE IF NOT EXISTS `portfolio_loss_simulation`(
  `iterationid` int, 
  `portfolio_loss` bigint, 
  `reporting_date` string,
  `version` int);


