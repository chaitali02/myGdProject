CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS profile_rule_results;
CREATE TABLE IF NOT EXISTS `profile_rule_results`(
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


