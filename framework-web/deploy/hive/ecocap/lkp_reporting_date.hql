CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS lkp_reporting_date;
CREATE  TABLE IF NOT EXISTS `lkp_reporting_date`(
  `reporting_date` string,
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");