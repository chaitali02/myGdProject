DROP TABLE IF EXISTS dim_country;
CREATE TABLE IF NOT EXISTS `dim_country`(
  `country_code` varchar(45) NOT NULL DEFAULT '',
  `country_name` varchar(45) NOT NULL DEFAULT '',
  `country_population` int(10) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`country_code`,`load_date`,`load_id`)
  );