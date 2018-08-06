CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_calc_final_cust_summary;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio_ul_calc_final_cust_summary`(
  `cust_id` string, 
  `portfolio_ul_cust_summary` double, 
  `reporting_date` string, 
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int);


