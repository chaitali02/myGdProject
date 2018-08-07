CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio`(
  `cust_id` varchar(45) DEFAULT NULL, 
  `industry` varchar(45) DEFAULT NULL, 
  `pd` double, 
  `exposure` int(11) DEFAULT NULL, 
  `lgd` double, 
  `lgd_var` int(11) DEFAULT NULL, 
  `correlation` double, 
  `sqrt_correlation` double, 
  `def_point` double, 
  `reporting_date` varchar(45) DEFAULT NULL, 
  `version` int(11) DEFAULT NULL);
LOAD DATA INFILE '/var/lib/mysql-files/customer_portfolio_1000.csv'  IGNORE INTO TABLE customer_portfolio FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;



