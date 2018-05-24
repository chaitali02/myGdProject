CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `dp_rule_results`;
CREATE TABLE `dp_rule_results` (
    `datapoduuid` varchar(45) DEFAULT NULL,
    `datapodversion` varchar(45) DEFAULT NULL,
    `datapodname` varchar(45) DEFAULT NULL,
    `attributeid` varchar(45) DEFAULT NULL,
    `attributename` varchar(45) DEFAULT NULL,
    `numrows` varchar(45) DEFAULT NULL,
    `minval` double precision,
    `maxval` double precision,
    `avgval` double precision,
    `medianval` double precision,
    `stddev` double precision,
    `numdistinct` int(11) DEFAULT NULL,
    `perdistinct` double precision,
    `numnull` int(11) DEFAULT NULL,
    `pernull` double precision,
    `sixsigma` double precision,
    `load_date` VARCHAR(45) DEFAULT NULL,
    `load_id` int(11) DEFAULT NULL,
    `version` int(11) DEFAULT NULL
);

