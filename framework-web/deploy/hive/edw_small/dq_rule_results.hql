CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS dq_rule_results;
CREATE  TABLE IF NOT EXISTS `dq_rule_results`(
  `rowkey` string,
  `datapoduuid` string,
  `datapodversion` string,
  `datapodname` string,
  `attributeid` string,
  `attributename` string,  
  `attributevalue` string,
  `nullcheck_pass` string,
  `valuecheck_pass` string,
  `rangecheck_pass` string,
  `datatypecheck_pass` string,
  `dataformatcheck_pass` string,
  `lengthcheck_pass` string,
  `refintegritycheck_pass` string,
  `dupcheck_pass` string,
  `customcheck_pass` string,
  `version` int);
  
