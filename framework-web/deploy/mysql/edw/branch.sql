CREATE DATABASE  IF NOT EXISTS `edw_small`;
USE `edw_small`;
DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch` (
  `branch_id` int(11) NOT NULL DEFAULT '0',
  `branch_type_id` int(11) DEFAULT NULL,
  `bank_id` varchar(45) DEFAULT NULL,
  `address_id` varchar(45) DEFAULT NULL,
  `branch_name` varchar(45) DEFAULT NULL,
  `branch_desc` varchar(45) DEFAULT NULL,
  `branch_contact_name` varchar(45) DEFAULT NULL,
  `branch_contact_phone` varchar(45) DEFAULT NULL,
  `branch_contact_email` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `version` int(11) DEFAULT NULL,
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_id`,`load_date`,`load_id`)
);
ALTER TABLE `branch` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/branch.csv'  IGNORE INTO TABLE branch FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

