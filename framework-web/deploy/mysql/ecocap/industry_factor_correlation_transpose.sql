DROP TABLE IF EXISTS `industry_factor_correlation_transpose`;

CREATE TABLE `industry_factor_correlation_transpose` (
  `factor_x` varchar(45) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `factor_y` varchar(45) DEFAULT NULL,
  `factor_value` double DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
