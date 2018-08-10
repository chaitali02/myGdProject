CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS customer_portfolio_clone;
CREATE  TABLE IF NOT EXISTS `customer_portfolio_clone`(
  `cust_id` string, 
  `industry` string, 
  `pd` double, 
  `exposure` int, 
  `lgd` double, 
  `lgd_var` int, 
  `correlation` double, 
  `sqrt_correlation` double, 
  `def_point` double, 
  `reporting_date` string, 
  `version` int);





