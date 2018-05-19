CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `transaction_id` varchar(45) DEFAULT NULL,
  `transaction_type_id` int(11) DEFAULT NULL,
  `account_id` varchar(45) DEFAULT NULL,
  `transaction_date` varchar(45) DEFAULT NULL,
  `from_account` varchar(45) DEFAULT NULL,
  `to_account` varchar(45) DEFAULT NULL,
  `amount_base_curr` decimal(30,2) DEFAULT NULL,
  `amount_usd` decimal(30,2) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `currency_rate` float DEFAULT NULL,
  `notes` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `load_id` int(11) DEFAULT NULL
);
ALTER TABLE `transaction` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/transaction.csv'  IGNORE INTO TABLE transaction FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

