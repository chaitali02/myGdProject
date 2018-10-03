DROP TABLE IF EXISTS dim_state;
CREATE TABLE IF NOT EXISTS `dim_state`(
  `state_code` varchar(45) NOT NULL DEFAULT '',
  `state_name` varchar(45) NOT NULL DEFAULT '',
  `country_code` varchar(45) NOT NULL DEFAULT '', 
  `state_population` int(10) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`state_code`,`load_date`,`load_id`)
  );