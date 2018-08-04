CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_loss_simulation;
CREATE TABLE IF NOT EXISTS `customer_loss_simulation`(
  `cust_id` varchar(45) DEFAULT NULL, 
  `iterationid` int(11) DEFAULT NULL, 
  `customer_loss` decimal, 
  `version` varchar(45) DEFAULT NULL)
   
