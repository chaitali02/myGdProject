CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_correlation;
CREATE TABLE IF NOT EXISTS `industry_factor_correlation`(
  `factor` varchar(45) DEFAULT NULL, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double, 
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL)
LOAD DATA INFILE '/var/lib/mysql-files/industry_factor_correlation.csv'  IGNORE INTO TABLE industry_factor_correlation.csv FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

