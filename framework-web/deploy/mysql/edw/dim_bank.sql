DROP TABLE IF EXISTS `dim_bank`;
CREATE TABLE `dim_bank` (
  `bank_id` varchar(45) NOT NULL DEFAULT '',
  `src_bank_id` varchar(45) DEFAULT NULL,
  `bank_code` varchar(45) DEFAULT NULL,
  `bank_name` varchar(45) DEFAULT NULL,
  `bank_account_number` varchar(45) DEFAULT NULL,
  `bank_currency_code` varchar(45) DEFAULT NULL,
  `bank_check_digits` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bank_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_bank_id` (`src_bank_id`,`load_date`,`load_id`)
);