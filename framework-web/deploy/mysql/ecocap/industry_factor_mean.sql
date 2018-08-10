
DROP TABLE IF EXISTS `industry_factor_mean`;

CREATE TABLE `industry_factor_mean` (
  `id` varchar(45) DEFAULT NULL,
  `mean` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;