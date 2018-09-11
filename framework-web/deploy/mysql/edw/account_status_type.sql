CREATE DATABASE  IF NOT EXISTS `edw_small`;
USE `edw_small`;
DROP TABLE IF EXISTS `account_status_type`;
CREATE TABLE `account_status_type` (
  `account_status_id` int(11) NOT NULL DEFAULT '0',
  `account_status_code` varchar(45) DEFAULT NULL,
  `account_status_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `version` varchar(45) DEFAULT NULL,
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_status_id`,`load_date`,`load_id`)
); 
ALTER TABLE `account_status_type` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/account_status_type.csv'   IGNORE INTO TABLE account_status_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;



