CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS dp_rule_results;
CREATE TABLE dp_rule_results
(
  `datapoduuid` string,
  `datapodversion` string,
  `datapodname` string,
  `attributeid` string,
  `attributename` string,
  `numrows` BIGINT,
  `minval` DOUBLE,
  `maxval` DOUBLE,
  `avgval` DOUBLE,
  `medianval` DOUBLE,
  `stddev` DOUBLE,
  `numdistinct` int,
  `perdistinct` DOUBLE,
  `numnull` int,
  `pernull` DOUBLE,
  `minlength` DOUBLE,
  `maxlength` DOUBLE,
  `avglength` DOUBLE,
  `numduplicates` DOUBLE,
  `version`        STRING 
);




