CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `bank`;
CREATE TABLE `bank` (
  `bank_id` int(11) NOT NULL DEFAULT '0',
  `bank_code` varchar(45) DEFAULT NULL,
  `bank_name` varchar(45) DEFAULT NULL,
  `bank_account_number` varchar(45) DEFAULT NULL,
  `bank_currency_code` varchar(45) DEFAULT NULL,
  `bank_check_digits` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `version` int(11) DEFAULT NULL,
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bank_id`,`load_date`,`load_id`)
);
ALTER TABLE `framework`.`bank` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/bank.csv'  IGNORE INTO TABLE bank FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

