DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_id` varchar(45) NOT NULL DEFAULT '',
  `address_line1` varchar(45) DEFAULT NULL,
  `address_line2` varchar(45) DEFAULT NULL,
  `address_line3` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `county` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `zipcode` int(11) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `latitude` varchar(45) DEFAULT NULL,
  `longitude` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`address_id`,`load_date`,`load_id`)
);