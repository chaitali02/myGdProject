CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS customer_portfolio_ul_calc_summary;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio_ul_calc_summary`(
  `cust_id` string,
  `portfolio_ul_cust_sum` double, 
  `portfolio_ul_total_sum` double,
  `reporting_date` string,
  `version` int);
  

