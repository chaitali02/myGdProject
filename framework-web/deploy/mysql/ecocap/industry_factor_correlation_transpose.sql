CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_correlation_transpose;
CREATE TABLE IF NOT EXISTS `industry_factor_correlation_transpose`(
  `factor_x` varchar(45) DEFAULT NULL, 
  `factor_y` varchar(45) DEFAULT NULL, 
  `factor_value` double,
  `reporting_date` varchar(45) DEFAULT NULL,  
  `version` int(11) DEFAULT NULL);
LOAD DATA INFILE '/var/lib/mysql-files/industry_factor_correlation_transpose.csv'  IGNORE INTO TABLE industry_factor_correlation_transpose.csv FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;


