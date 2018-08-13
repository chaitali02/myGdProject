CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `industry_factor_transpose`;

CREATE TABLE `industry_factor_transpose` (
  `iteration_id` int(11) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `factor` varchar(45) DEFAULT NULL,
  `factor_value` double DEFAULT NULL,
  `version` varchar(45) DEFAULT NULL
);

