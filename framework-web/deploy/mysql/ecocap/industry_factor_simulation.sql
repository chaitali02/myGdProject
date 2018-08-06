CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_simulation;
CREATE TABLE IF NOT EXISTS `industry_factor_simulation`(
  `iteration_id` int(11) DEFAULT NULL, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `version` int(11) DEFAULT NULL);
LOAD DATA INFILE '/var/lib/mysql-files/industry_factor_simulation.csv'  IGNORE INTO TABLE industry_factor_simulation.csv FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;



