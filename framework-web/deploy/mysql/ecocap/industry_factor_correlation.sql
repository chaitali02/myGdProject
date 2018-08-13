CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `industry_factor_correlation`;

CREATE TABLE `industry_factor_correlation` (
  `factor` varchar(45) DEFAULT NULL,
  `factor1` double DEFAULT NULL,
  `factor2` double DEFAULT NULL,
  `factor3` double DEFAULT NULL,
  `factor4` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_factor_correlation_2018.csv'  IGNORE INTO TABLE industry_factor_correlation FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' IGNORE 1 LINES;

