CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS customer_portfolio_ul_x;
CREATE  TABLE IF NOT EXISTS `customer_portfolio_ul_x`(
  `cust_id` string, 
  `industry` string, 
  `pd` double, 
  `exposure` int,
  `lgd` double, 
  `lgd_var` int, 
  `correlation` double, 
  `sqrt_correlation` double, 
  `def_point` double, 
  `unexpected_loss` double, 
  `reporting_date` string, 
  `version` int);


