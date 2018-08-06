CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_es_allocation;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_es_allocation`(
  `cust_id` varchar(45) DEFAULT NULL, 
  `es_contribution` decimal, 
  `es_allocation` decimal);
   
