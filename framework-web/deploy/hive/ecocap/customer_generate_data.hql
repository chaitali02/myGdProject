CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
alter table customer_generate_data  set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS customer_generate_data;

CREATE  TABLE IF NOT EXISTS `customer_generate_data`(
  `id` int, 
  `data` double,
  `version` int);


