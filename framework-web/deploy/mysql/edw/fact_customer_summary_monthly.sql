DROP TABLE IF EXISTS `fact_customer_summary_monthly`;
CREATE TABLE `fact_customer_summary_monthly` (
  `customer_id` varchar(45) NOT NULL DEFAULT '',
  `yyyy_mm` varchar(45) NOT NULL DEFAULT '',
  `total_trans_count` varchar(45) DEFAULT NULL,
  `total_trans_amount_usd` int(11) DEFAULT NULL,
  `avg_trans_amount` int(11) DEFAULT NULL,
  `min_amount` decimal(38,2) DEFAULT NULL,
  `max_amount` decimal(38,2) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`,`yyyy_mm`,`load_date`,`load_id`)
);