CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS profile_rule_results;
CREATE EXTERNAL TABLE IF NOT EXISTS `profile_rule_results`(
  `DatapodUUID` string, 
  `DatapodVersion` string, 
  `AttributeId` string, 
  `minVal` double, 
  `maxVal` double, 
  `avgVal` double, 
  `medianVal` double, 
  `stdDev` double, 
  `numDistinct` int, 
  `perDistinct` double, 
  `numNull` int, 
  `perNull` double, 
  `sixSigma` double, 
  `version` int)


