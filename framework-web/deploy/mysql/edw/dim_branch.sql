CREATE DATABASE  IF NOT EXISTS `edw_small`;
USE `edw_small`;
DROP TABLE IF EXISTS `dim_branch`;
CREATE TABLE `dim_branch` (
  `branch_id` varchar(45) NOT NULL DEFAULT '',
  `src_branch_id` varchar(45) DEFAULT NULL,
  `branch_type_code` varchar(45) DEFAULT NULL,
  `branch_name` varchar(45) DEFAULT NULL,
  `branch_desc` varchar(45) DEFAULT NULL,
  `branch_contact_name` varchar(45) DEFAULT NULL,
  `branch_contact_phone` varchar(45) DEFAULT NULL,
  `branch_contact_email` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_branch_id` (`src_branch_id`,`load_date`,`load_id`)
);
ALTER TABLE `dim_branch` PARTITION BY KEY(load_date,load_id) PARTITIONS 2;
