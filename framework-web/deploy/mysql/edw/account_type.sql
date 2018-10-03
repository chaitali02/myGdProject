DROP TABLE IF EXISTS `account_type`;
CREATE TABLE `account_type` (
  `account_type_id` int(11) NOT NULL DEFAULT '0',
  `account_type_code` varchar(45) DEFAULT NULL,
  `account_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_type_id`,`load_date`,`load_id`)
);