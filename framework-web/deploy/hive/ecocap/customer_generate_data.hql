CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_generate_data;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_generate_data`(
  `id` int, 
  `data` double,
  `version` int);


