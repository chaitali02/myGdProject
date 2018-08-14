CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `customer_idiosyncratic_transpose`;

CREATE TABLE `customer_idiosyncratic_transpose` (
  `iterationid` int(11) DEFAULT NULL,
  `reporting_date` varchar(50) DEFAULT NULL,
  `customer` varchar(45) DEFAULT NULL,
  `pd` double DEFAULT NULL,
  `version` varchar(45) DEFAULT NULL
);


