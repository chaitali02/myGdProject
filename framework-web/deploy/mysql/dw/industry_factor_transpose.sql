CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `industry_factor_transpose`;
CREATE TABLE `industry_factor_transpose` (
    `iteration_id` int(11) DEFAULT NULL,
    `factor`  varchar(45) DEFAULT NULL,
    `factor_value` double precision,
    `version` int(11) DEFAULT NULL
);

