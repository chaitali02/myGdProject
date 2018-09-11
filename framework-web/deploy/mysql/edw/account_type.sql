CREATE DATABASE  IF NOT EXISTS `edw_small`;
USE `edw_small`;
DROP TABLE IF EXISTS `account_type`;
CREATE TABLE `account_type` (
  `account_type_id` int(11) NOT NULL DEFAULT '0',
  `account_type_code` varchar(45) DEFAULT NULL,
  `account_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `version` int(11) DEFAULT NULL,
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_type_id`,`load_date`,`load_id`)
);
ALTER TABLE `account_type` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/account_type.csv' IGNORE INTO TABLE account_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;


