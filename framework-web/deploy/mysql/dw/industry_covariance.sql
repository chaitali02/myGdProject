CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `industry_covariance`;
CREATE TABLE `industry_covariance` (
    `id` int(11) DEFAULT NULL,
    `automobile` double precision,
    `pharma` double precision,
    `finance` double precision    
);

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_covariance.csv' IGNORE INTO TABLE industry_covariance FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;


