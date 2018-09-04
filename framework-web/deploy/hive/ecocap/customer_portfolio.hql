DROP TABLE IF EXISTS customer_portfolio;
CREATE  TABLE IF NOT EXISTS `customer_portfolio`(
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
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';