DROP TABLE IF EXISTS `transaction_type`;
CREATE TABLE `transaction_type` (
  `transaction_type_id` varchar(45) NOT NULL DEFAULT '0',
  `transaction_type_code` varchar(45) DEFAULT NULL,
  `transaction_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`transaction_type_id`,`load_date`,`load_id`)  
);