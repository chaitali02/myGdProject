CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `industry_factor_correlation_transpose`;

CREATE TABLE `industry_factor_correlation_transpose` (
  `factor_x` varchar(45) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `factor_y` varchar(45) DEFAULT NULL,
  `factor_value` double DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_factor_correlation_transpose_2018.csv'  IGNORE INTO TABLE industry_factor_correlation_transpose FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' IGNORE 1 LINES;

