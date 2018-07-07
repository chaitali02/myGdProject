CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `product_type`;
CREATE TABLE `product_type` (
  `product_type_id` int(11) DEFAULT NULL,
  `product_type_code` varchar(45) DEFAULT NULL,
  `product_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `load_id` varchar(45) DEFAULT NULL
);
ALTER TABLE `product_type` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/product_type.csv'  IGNORE INTO TABLE product_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

