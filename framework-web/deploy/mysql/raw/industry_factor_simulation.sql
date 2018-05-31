CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `industry_factor_simulation`;
CREATE TABLE `industry_factor_simulation` (
     `iteration_id` int(11) DEFAULT NULL,
    `factor1` double precision,
    `factor2` double precision,
    `factor3` double precision,
    `factor4` double precision,
    `version` int(11) DEFAULT NULL
);

