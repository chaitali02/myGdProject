CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `profile_rule_results`;
CREATE TABLE `profile_rule_results` (
  `DatapodUUID` varchar(45) DEFAULT NULL,
  `DatapodVersion` varchar(45) DEFAULT NULL,
  `AttributeId` varchar(45) DEFAULT NULL,
  `minVal` double DEFAULT NULL,
  `maxVal` double DEFAULT NULL,
  `avgVal` double DEFAULT NULL,
  `medianVal` double DEFAULT NULL,
  `stdDev` double DEFAULT NULL,
  `numDistinct` int(11) DEFAULT NULL,
  `perDistinct` double DEFAULT NULL,
  `numNull` int(11) DEFAULT NULL,
  `perNull` double DEFAULT NULL,
  `sixSigma` double DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);
 
