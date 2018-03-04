CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `dim_customer`;
CREATE TABLE `dim_customer` (
  `customer_id` varchar(45) NOT NULL DEFAULT '',
  `src_customer_id` varchar(45) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `address_line1` varchar(45) DEFAULT NULL,
  `address_line2` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `date_first_purchase` varchar(45) DEFAULT NULL,
  `commute_distance` int(11) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `postal_code` varchar(45) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_customer_id` (`src_customer_id`,`load_date`,`load_id`)
);
ALTER TABLE `dim_customer` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
