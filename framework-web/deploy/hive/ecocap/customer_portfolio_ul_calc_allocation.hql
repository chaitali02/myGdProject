DROP TABLE IF EXISTS customer_portfolio_ul_calc_allocation;
CREATE  TABLE IF NOT EXISTS `customer_portfolio_ul_calc_allocation`(
  `cust_id` string, 
  `portfolio_ul_cust_allocation` double, 
  `reporting_date` string,
  `version` int);