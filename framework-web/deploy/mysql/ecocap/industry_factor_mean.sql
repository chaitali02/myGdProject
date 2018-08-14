CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `industry_factor_mean`;

CREATE TABLE `industry_factor_mean` (
  `id` varchar(45) DEFAULT NULL,
  `mean` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_factor_mean_2018.csv'  IGNORE INTO TABLE industry_factor_mean FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' IGNORE 1 LINES;
