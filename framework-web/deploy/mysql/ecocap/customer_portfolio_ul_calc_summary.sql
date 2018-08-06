CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_calc_summary;
CREATE TABLE IF NOT EXISTS `customer_portfolio_ul_calc_summary`(
  `portfolio_ul_calc_sum` double, 
  `reporting_date` varchar(45) DEFAULT NULL
  `version` int(11) DEFAULT NULL)
   
