CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE `industry_factor_simulation_stage`;

CREATE TABLE  `industry_factor_simulation_stage`(
`iteration_id` int(11) DEFAULT NULL,
`factor1` double DEFAULT NULL,
`factor2` double DEFAULT NULL,
`factor3` double DEFAULT NULL,
`factor4` double DEFAULT NULL,
`reporting_date` varchar(45) DEFAULT NULL,
`version` int(11) DEFAULT NULL
);




