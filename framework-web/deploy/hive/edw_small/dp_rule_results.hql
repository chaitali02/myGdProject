CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS dp_rule_results;
CREATE TABLE dp_rule_results
(
  `datapoduuid` string,
  `datapodversion` string,
  `datapodname` string,
  `attributeid` string,
  `attributename` string,
  `numrows` string,
  `minval` BIGINT,
  `maxval` BIGINT,
  `avgval` BIGINT,
  `medianval` BIGINT,
  `stddev` BIGINT,
  `numdistinct` int,
  `perdistinct` BIGINT,
  `numnull` int,
  `pernull` BIGINT,
  `sixsigma` BIGINT,
  `load_date` string,
  `load_id` int,
  `version` int
);


