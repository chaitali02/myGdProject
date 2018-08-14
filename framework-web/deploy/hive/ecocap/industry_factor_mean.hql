CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table industry_factor_mean set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS industry_factor_mean;

CREATE TABLE IF NOT EXISTS `industry_factor_mean`(
  `id` string, 
  `mean` double, 
  `reporting_date` string,
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_mean_2018.csv' OVERWRITE INTO TABLE ecocap.industry_factor_mean;

