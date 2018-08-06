CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_calc;
CREATE TABLE IF NOT EXISTS `customer_portfolio_ul_calc`(
  `cust_id1` varchar(45) DEFAULT NULL, 
  `industry1` varchar(45) DEFAULT NULL, 
  `correlation1` double, 
  `unexpected_loss1` double,
  `cust_id2` double, 
  `industry2` varchar(45) DEFAULT NULL, 
  `correlation2` double, 
  `unexpected_loss2` double, 
  `factor_value` double, 
  `portfolio_ul_calc` double, 
  `reporting_date` varchar(45) DEFAULT NULL, 
  `version` int(11) DEFAULT NULL);
   
