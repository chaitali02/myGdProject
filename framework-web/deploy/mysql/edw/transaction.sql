DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `transaction_id` varchar(45) NOT NULL DEFAULT '0',
  `transaction_type_id` int(11) DEFAULT NULL,
  `account_id` varchar(45) DEFAULT NULL,
  `transaction_date` varchar(45) DEFAULT NULL,
  `from_account` varchar(45) DEFAULT NULL,
  `to_account` varchar(45) DEFAULT NULL,
  `amount_base_curr` decimal(30,2) DEFAULT NULL,
  `amount_usd` decimal(30,2) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `currency_rate` float DEFAULT NULL,
  `notes` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`transaction_id`,`load_date`,`load_id`)    
);