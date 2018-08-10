DROP TABLE IF EXISTS `customer_es_allocation`;

CREATE TABLE `customer_es_allocation` (
  `cust_id` varchar(50) DEFAULT NULL,
  `es_contribution` decimal(10,0) DEFAULT NULL,
  `es_allocation` decimal(10,0) DEFAULT NULL,
  `reporting_date` varchar(50) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

