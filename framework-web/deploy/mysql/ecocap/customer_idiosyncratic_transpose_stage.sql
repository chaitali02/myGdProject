CREATE DATABASE IF NOT EXISTS `ecocap`;
use `ecocap`;
DROP TABLE IF EXISTS `customer_idiosyncratic_transpose_stage`;

CREATE TABLE IF NOT EXISTS `customer_idiosyncratic_transpose_stage`(
  `iterationid` int(11) DEFAULT NULL, 
  `customer` varchar(50) DEFAULT NULL, 
  `pd` double DEFAULT NULL, 
  `version` int(11) DEFAULT NULL
);

  
