CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `account_id` varchar(45) NOT NULL DEFAULT '',
  `account_type_id` int(11) DEFAULT NULL,
  `account_status_id` int(11) DEFAULT NULL,
  `product_type_id` int(11) DEFAULT NULL,
  `customer_id` varchar(45) DEFAULT NULL,
  `pin_number` int(11) DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  `primary_iden_doc` varchar(45) DEFAULT NULL,
  `primary_iden_doc_id` varchar(45) DEFAULT NULL,
  `secondary_iden_doc` varchar(45) DEFAULT NULL,
  `secondary_iden_doc_id` varchar(45) DEFAULT NULL,
  `account_open_date` varchar(45) DEFAULT NULL,
  `account_number` varchar(45) DEFAULT NULL,
  `opening_balance` varchar(45) DEFAULT NULL,
  `current_balance` varchar(45) DEFAULT NULL,
  `overdue_balance` int(11) DEFAULT NULL,
  `overdue_date` varchar(45) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `interest_type` varchar(45) DEFAULT NULL,
  `interest_rate` float DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `version` varchar(45) DEFAULT NULL,
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`load_date`,`load_id`)
);
ALTER TABLE `framework`.`account` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/account.csv'  IGNORE INTO TABLE account FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;



