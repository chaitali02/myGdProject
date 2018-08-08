CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS industry_factor_mean_adhoc;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_mean_adhoc`(
  `id` string, 
  `mean` double,
  `version` int);

