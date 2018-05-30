CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `fact_account_summary_monthly`;
CREATE TABLE `fact_account_summary_monthly` (
  `account_id` varchar(45) NOT NULL DEFAULT '',
  `yyyy_mm` varchar(45) NOT NULL DEFAULT '',
  `total_trans_count` bigint(20) DEFAULT NULL,
  `total_trans_amount_usd` int(11) DEFAULT NULL,
  `avg_trans_amount` int(11) DEFAULT NULL,
  `min_amount` decimal(38,2) DEFAULT NULL,
  `max_amount` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`yyyy_mm`,`load_date`,`load_id`)
);
ALTER TABLE `fact_account_summary_monthly` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
