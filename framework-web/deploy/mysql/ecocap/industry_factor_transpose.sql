CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_transpose;
CREATE TABLE IF NOT EXISTS `industry_factor_transpose`(
  `iteration_id` int(11) DEFAULT NULL, 
  `factor` varchar(45) DEFAULT NULL, 
  `factor_value` double,
  `version` varchar(45) DEFAULT NULL);
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_transpose.csv' OVERWRITE INTO TABLE industry_factor_transpose PARTITION (load_date='2017-07-01', load_id='00');


