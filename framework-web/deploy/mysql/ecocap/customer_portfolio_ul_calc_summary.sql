CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `customer_portfolio_ul_calc_summary`;

CREATE TABLE `customer_portfolio_ul_calc_summary` (
  `cust_id` varchar(45) DEFAULT NULL,
  `portfolio_ul_cust_sum` double DEFAULT NULL,
  `portfolio_ul_total_sum` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);


