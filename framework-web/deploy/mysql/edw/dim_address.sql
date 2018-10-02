DROP TABLE IF EXISTS `dim_address`;
CREATE TABLE `dim_address` (
  `address_id` varchar(45) NOT NULL DEFAULT '',
  `src_address_id` varchar(45) DEFAULT NULL,
  `address_line1` varchar(45) DEFAULT NULL,
  `address_line2` varchar(45) DEFAULT NULL,
  `address_line3` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `county` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `zipcode` int(11) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `latitude` varchar(45) DEFAULT NULL,
  `longtitude` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`address_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_address_id` (`src_address_id`,`load_date`,`load_id`)
);