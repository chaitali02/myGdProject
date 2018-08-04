CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_idiosyncratic_transpose;
CREATE TABLE IF NOT EXISTS `customer_idiosyncratic_transpose`(
  `iterationid` int(11) DEFAULT NULL, 
  `customer` varchar(45) DEFAULT NULL, 
  `pd` double, 
  `version` varchar(45) DEFAULT NULL);


