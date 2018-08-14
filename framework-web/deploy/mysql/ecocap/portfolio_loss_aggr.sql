CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `portfolio_loss_aggr`;

CREATE TABLE `portfolio_loss_aggr` (
  `expected_loss` decimal(10,0) DEFAULT NULL,
  `value_at_risk` decimal(10,0) DEFAULT NULL,
  `economic_capital` decimal(10,0) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

