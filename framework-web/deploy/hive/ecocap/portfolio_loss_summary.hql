CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_loss_summary;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_loss_summary`(
  `portfolio_avg_pd` int, 
  `portfolio_avg_lgd` bigint, 
  `portfolio_total_ead` double,
  `portfolio_expected_loss` double,
  `portfolio_value_at_risk` double,
  `portfolio_economic_capital` double,
  `portfolio_expected_sum` double,
  `portfolio_es_percentage` double,
  `portfolio_val_percentage` double,
  `portfolio_el_percentage` double,
  `reporting_date` string,
  `version` int);


