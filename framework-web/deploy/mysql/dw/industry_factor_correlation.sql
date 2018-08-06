CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `industry_factor_correlation`;
CREATE TABLE `industry_factor_correlation` (
    `factor` varchar(45) DEFAULT NULL,
    `factor1` double precision,
    `factor2` double precision,
    `factor3` double precision,
    `factor4` double precision,
    `reporting_date` varchar(45) DEFAULT NULL,
    `version` int(11) DEFAULT NULL
);

