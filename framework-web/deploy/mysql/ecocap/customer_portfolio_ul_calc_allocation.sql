CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_calc_allocation;
CREATE TABLE IF NOT EXISTS `customer_portfolio_ul_calc_allocation`(
  `cust_id` varchar(45) DEFAULT NULL, 
  `portfolio_ul_cust_allocation` double, 
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL)
   
