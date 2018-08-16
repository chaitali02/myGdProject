CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS customer_es_allocation;
CREATE  TABLE IF NOT EXISTS `customer_es_allocation`(
  `cust_id` string, 
  `es_contribution` decimal, 
  `es_allocation` decimal,
  `reporting_date` string,
  `version` int);