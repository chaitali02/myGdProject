CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `portfolio_expected_sum`;

CREATE TABLE `portfolio_expected_sum` (
  `expected_sum` decimal(10,0) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

