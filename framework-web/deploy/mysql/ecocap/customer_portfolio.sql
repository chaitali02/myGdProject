CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `customer_portfolio`;

CREATE TABLE `customer_portfolio` (
  `cust_id` varchar(45) DEFAULT NULL,
  `industry` varchar(45) DEFAULT NULL,
  `pd` double DEFAULT NULL,
  `exposure` int(11) DEFAULT NULL,
  `lgd` double DEFAULT NULL,
  `lgd_var` int(11) DEFAULT NULL,
  `correlation` double DEFAULT NULL,
  `sqrt_correlation` double DEFAULT NULL,
  `def_point` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);


LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/customer_portfolio_1000_2018.csv'  IGNORE INTO TABLE customer_portfolio FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;




