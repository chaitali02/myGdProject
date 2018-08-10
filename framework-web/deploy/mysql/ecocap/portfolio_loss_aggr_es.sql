DROP TABLE IF EXISTS `portfolio_loss_aggr_es`;

CREATE TABLE `portfolio_loss_aggr_es` (
  `expected_loss` decimal(10,0) DEFAULT NULL,
  `value_at_risk` decimal(10,0) DEFAULT NULL,
  `economic_capital` decimal(10,0) DEFAULT NULL,
  `expected_sum` decimal(10,0) DEFAULT NULL,
  `reporting_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;