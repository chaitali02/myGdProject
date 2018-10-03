DROP TABLE IF EXISTS `dim_account`;
CREATE TABLE `dim_account` (
  `account_id` varchar(45) NOT NULL DEFAULT '',
  `src_account_id` varchar(45) DEFAULT NULL,
  `account_type_code` varchar(45) DEFAULT NULL,
  `account_status_code` varchar(45) DEFAULT NULL,
  `product_type_code` varchar(45) DEFAULT NULL,
  `pin_number` int(11) DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  `primary_iden_doc` varchar(45) DEFAULT NULL,
  `primary_iden_doc_id` varchar(45) DEFAULT NULL,
  `secondary_iden_doc` varchar(45) DEFAULT NULL,
  `secondary_iden_doc_id` varchar(45) DEFAULT NULL,
  `account_open_date` varchar(45) DEFAULT NULL,
  `account_number` varchar(45) DEFAULT NULL,
  `opening_balance` varchar(45) DEFAULT NULL,
  `current_balance` varchar(45) DEFAULT NULL,
  `overdue_balance` int(11) DEFAULT NULL,
  `overdue_date` varchar(45) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `interest_type` varchar(45) DEFAULT NULL,
  `interest_rate` float DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_account_id` (`src_account_id`,`load_date`,`load_id`)
);