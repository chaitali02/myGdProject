CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul;
CREATE TABLE IF NOT EXISTS `customer_portfolio_ul`(
  `cust_id` varchar(45) DEFAULT NULL, 
  `industry` varchar(45) DEFAULT NULL, 
  `pd` double, 
  `exposure` int(11) DEFAULT NULL,
  `lgd` double, 
  `lgd_var` int(11) DEFAULT NULL, 
  `correlation` double, 
  `sqrt_correlation` double, 
  `def_point` double, 
  `unexpected_loss` double, 
  `reporting_date` varchar(45) DEFAULT NULL, 
  `version` int(11) DEFAULT NULL);
  
