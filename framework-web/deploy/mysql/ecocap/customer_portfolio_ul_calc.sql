CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `customer_portfolio_ul_calc`;

CREATE TABLE `customer_portfolio_ul_calc` (
  `cust_id1` varchar(45) DEFAULT NULL,
  `industry1` varchar(45) DEFAULT NULL,
  `correlation1` double DEFAULT NULL,
  `unexpected_loss1` double DEFAULT NULL,
  `cust_id2` double DEFAULT NULL,
  `industry2` varchar(45) DEFAULT NULL,
  `correlation2` double DEFAULT NULL,
  `unexpected_loss2` double DEFAULT NULL,
  `factor_value` double DEFAULT NULL,
  `portfolio_ul_calc` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

