DROP TABLE IF EXISTS `account_status_type`;
CREATE TABLE `account_status_type` (
  `account_status_id` int(11) NOT NULL DEFAULT '0',
  `account_status_code` varchar(45) DEFAULT NULL,
  `account_status_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_status_id`,`load_date`,`load_id`)
);