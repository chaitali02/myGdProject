CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_simulation;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_simulation`(
  `iteration_id` int, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.industry_factor_simulation ADD PARTITION(load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_simulation.csv' OVERWRITE INTO TABLE industry_factor_simulation PARTITION (load_date='2017-07-01', load_id='00');



