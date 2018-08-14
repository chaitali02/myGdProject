CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table portfolio_loss_histogram_percentage set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS portfolio_loss_histogram_percentage;

CREATE  TABLE IF NOT EXISTS `portfolio_loss_histogram_percentage`(
  `bucket` string, 
  `frequency` int, 
  `version` string);
  

