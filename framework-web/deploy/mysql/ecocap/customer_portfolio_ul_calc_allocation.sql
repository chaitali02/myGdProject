DROP TABLE IF EXISTS `customer_portfolio_ul_calc_allocation`;

CREATE TABLE `customer_portfolio_ul_calc_allocation` (
  `cust_id` varchar(45) DEFAULT NULL,
  `portfolio_ul_cust_allocation` double DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

