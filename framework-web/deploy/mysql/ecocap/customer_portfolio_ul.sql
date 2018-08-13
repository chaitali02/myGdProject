CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `customer_portfolio_ul`;

CREATE TABLE `customer_portfolio_ul` (
  `cust_id` varchar(45) DEFAULT NULL,
  `industry` varchar(45) DEFAULT NULL,
  `pd` double DEFAULT NULL,
  `exposure` int(11) DEFAULT NULL,
  `lgd` double DEFAULT NULL,
  `lgd_var` int(11) DEFAULT NULL,
  `correlation` double DEFAULT NULL,
  `sqrt_correlation` double DEFAULT NULL,
  `def_point` double DEFAULT NULL,
  `unexpected_loss` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);



