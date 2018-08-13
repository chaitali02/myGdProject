CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE `industry_factor_simulation_adhoc`;

CREATE TABLE  `industry_factor_simulation_adhoc`(
`iteration_id` int(11) DEFAULT NULL,
`factor1` double DEFAULT NULL,
`factor2` double DEFAULT NULL,
`factor3` double DEFAULT NULL,
`factor4` double DEFAULT NULL,
`version` int(11) DEFAULT NULL
);



