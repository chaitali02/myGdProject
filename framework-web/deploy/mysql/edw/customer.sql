DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `customer_id` varchar(45) NOT NULL DEFAULT '',
  `address_id` varchar(45) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ssn` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `date_first_purchase` varchar(45) DEFAULT NULL,
  `commute_distance_miles` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`,`load_date`,`load_id`)
);