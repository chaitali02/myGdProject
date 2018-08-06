CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `industry_mean`;
CREATE TABLE `industry_mean` (
    `id` int(11) DEFAULT NULL,
    `mean` double precision,
    `version` int(11) DEFAULT NULL
);

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_covariance.csv' IGNORE INTO TABLE industry_covariance FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' IGNORE 1 LINES;
