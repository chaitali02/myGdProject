CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `dq_rule_results`;
CREATE TABLE `dq_rule_results` (
  `rowkey` varchar(45) DEFAULT NULL,
  `datapoduuid` varchar(45) DEFAULT NULL,
  `datapodversion` varchar(45) DEFAULT NULL,  
  `datapodname` varchar(45) DEFAULT NULL,
  `attributeid` varchar(45) DEFAULT NULL,
  `attributename` varchar(45) DEFAULT NULL,
  `attributevalue` varchar(45) DEFAULT NULL,
  `nullcheck_pass` varchar(45) DEFAULT NULL,
  `valuecheck_pass` varchar(45) DEFAULT NULL,
  `rangecheck_pass` varchar(45) DEFAULT NULL,
  `datatypecheck_pass` varchar(45) DEFAULT NULL,
  `dataformatcheck_pass` varchar(45) DEFAULT NULL,
  `lengthcheck_pass` varchar(45) DEFAULT NULL,
  `refintegritycheck_pass` varchar(45) DEFAULT NULL,
  `dupcheck_pass` varchar(45) DEFAULT NULL,
  `customcheck_pass` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);

