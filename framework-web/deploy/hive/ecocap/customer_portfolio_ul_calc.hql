CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
alter table customer_portfolio_ul_calc set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS customer_portfolio_ul_calc;
CREATE  TABLE IF NOT EXISTS `customer_portfolio_ul_calc`(
  `cust_id1` string, 
  `industry1` string, 
  `correlation1` double, 
  `unexpected_loss1` double,
  `cust_id2` double, 
  `industry2` string, 
  `correlation2` double, 
  `unexpected_loss2` double, 
  `factor_value` double, 
  `portfolio_ul_calc` double, 
  `reporting_date` string, 
  `version` int);