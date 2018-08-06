CREATE DATABASE  IF NOT EXISTS `framework` ;
USE `framework`;
DROP TABLE IF EXISTS `transaction_type`;
CREATE TABLE `transaction_type` (
  `transaction_type_id` varchar(45) DEFAULT NULL,
  `transaction_type_code` varchar(45) DEFAULT NULL,
  `transaction_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `load_id` int(11) DEFAULT NULL
);
ALTER TABLE `transaction_type` PARTITION BY KEY(load_date,load_id) PARTITIONS 2; 
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/transaction_type.csv'  IGNORE INTO TABLE transaction_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

