CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `branch_type`;
CREATE TABLE `branch_type` (
  `branch_type_id` int(11) NOT NULL DEFAULT '0',
  `branch_type_code` varchar(45) DEFAULT NULL,
  `branch_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `version` int(11) DEFAULT NULL,
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_type_id`,`load_date`,`load_id`)
);
ALTER TABLE `framework`.`branch_type` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/branch_type.csv'  IGNORE INTO TABLE branch_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

