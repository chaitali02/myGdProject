CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `rc_rule_results`;
CREATE TABLE `rc_rule_results` (
    `sourcedatapoduuid` varchar(45) DEFAULT NULL,
    `sourcedatapodversion` varchar(45) DEFAULT NULL,
    `sourcedatapodname` varchar(45) DEFAULT NULL,
    `sourceattributeid` varchar(45) DEFAULT NULL,
    `sourceattributename` varchar(45) DEFAULT NULL,
    `sourcevalue` double DEFAULT NULL,
    `targetdatapoduuid` varchar(45) DEFAULT NULL,
    `targetdatapodversion` varchar(45) DEFAULT NULL,
    `targetdatapodname` varchar(45) DEFAULT NULL,
    `targetattributeid` varchar(45) DEFAULT NULL,
    `targetattributename` varchar(45) DEFAULT NULL,
    `targetvalue`  double DEFAULT NULL,
    `status` varchar(45) DEFAULT NULL,
    `version` int(11) DEFAULT NULL
);

