DROP TABLE IF EXISTS `product_type`;
CREATE TABLE `product_type` (
  `product_type_id` int(11) NOT NULL DEFAULT '0',
  `product_type_code` varchar(45) DEFAULT NULL,
  `product_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`product_type_id`,`load_date`,`load_id`)  
);