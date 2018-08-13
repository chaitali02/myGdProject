CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_loss_histogram;
CREATE  TABLE IF NOT EXISTS `portfolio_loss_histogram`(
  `bucket` decimal, 
  `frequency` int, 
  `version` string);

alter table portfolio_loss_histogram set tblproperties('EXTERNAL'='FALSE');
