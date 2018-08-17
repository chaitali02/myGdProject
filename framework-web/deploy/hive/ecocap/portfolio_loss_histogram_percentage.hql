CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_loss_histogram_percentage;
CREATE  TABLE IF NOT EXISTS `portfolio_loss_histogram_percentage`(
  `reporting_date` string,
  `bucket` string, 
  `frequency` int, 
  `version` int);