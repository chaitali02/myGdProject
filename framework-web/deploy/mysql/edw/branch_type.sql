DROP TABLE IF EXISTS `branch_type`;
CREATE TABLE `branch_type` (
  `branch_type_id` int(11) NOT NULL DEFAULT '0',
  `branch_type_code` varchar(45) DEFAULT NULL,
  `branch_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_type_id`,`load_date`,`load_id`)
);