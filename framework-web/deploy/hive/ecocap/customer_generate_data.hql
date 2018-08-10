CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS customer_generate_data;
CREATE  TABLE IF NOT EXISTS `customer_generate_data`(
  `id` int, 
  `data` double,
  `version` int);


