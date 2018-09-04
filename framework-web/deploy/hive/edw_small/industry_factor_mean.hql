CREATE DATABASE IF NOT EXISTS `edw_small`;
USE `edw_small`;
DROP TABLE IF EXISTS `industry_factor_mean`;
CREATE TABLE `industry_factor_mean` (
    `id` string,
    `mean` double,
    `reporting_date` string,
    `version` int
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
LOAD DATA LOCAL INPATH '/user/hive/warehouse/edw_small/upload/industry_factor_mean_small.csv' OVERWRITE INTO TABLE edw_small.industry_factor_mean
