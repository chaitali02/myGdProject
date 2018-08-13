CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE `industry_factor_mean_adhoc`;

CREATE TABLE `industry_factor_mean_adhoc` (
`cust_id` varchar(45) DEFAULT NULL,
`mean` double DEFAULT NULL,
`version` int(11) DEFAULT NULL
);

