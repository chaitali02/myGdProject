CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `portfolio_loss_simulation`;

CREATE TABLE `portfolio_loss_simulation` (
  `iterationid` int(11) DEFAULT NULL,
  `portfolio_loss` bigint(20) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

