CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_generate_data;
CREATE TABLE IF NOT EXISTS `customer_generate_data`(
  `id` int(11) DEFAULT NULL, 
  `data` double,
  `version` varchar(45) DEFAULT NULL)

