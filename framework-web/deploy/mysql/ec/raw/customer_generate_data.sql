CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_generate_data;
CREATE TABLE IF NOT EXISTS `customer_generate_data`(
  `id` int(11) DEFAULT NULL, 
  `data` double,
  `version` varchar(45) DEFAULT NULL)
   PARTITIONED BY ( 
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int bigint(20) NOT NULL DEFAULT '0')
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");

