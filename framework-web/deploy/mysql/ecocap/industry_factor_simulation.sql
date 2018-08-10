DROP TABLE IF EXISTS `industry_factor_simulation`;

CREATE TABLE `industry_factor_simulation` (
  `iteration_id` int(11) DEFAULT NULL,
  `factor1` double DEFAULT NULL,
  `factor2` double DEFAULT NULL,
  `factor3` double DEFAULT NULL,
  `factor4` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

