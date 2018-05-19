CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `dim_transaction_type`;
CREATE TABLE `dim_transaction_type` (
  `transaction_type_id` varchar(45) NOT NULL DEFAULT '',
  `src_transaction_type_id` int(11) DEFAULT NULL,
  `transaction_type_code` varchar(45) DEFAULT NULL,
  `transaction_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`transaction_type_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_transaction_type_id` (`src_transaction_type_id`,`load_date`,`load_id`)
);
ALTER TABLE `dim_transaction_type` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
