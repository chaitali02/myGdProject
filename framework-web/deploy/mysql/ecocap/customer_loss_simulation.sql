DROP TABLE IF EXISTS `customer_loss_simulation`;

CREATE TABLE `customer_loss_simulation` (
  `cust_id` varchar(45) DEFAULT NULL,
  `iterationid` int(11) DEFAULT NULL,
  `customer_loss` decimal(10,0) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


