CREATE DATABASE IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `industry_factor_mean`;
CREATE TABLE `industry_factor_mean` (
    `id` string,
    `mean` double,
    `reporting_date` string,
    `version` int
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_mean.csv' OVERWRITE INTO TABLE framework.industry_factor_mean
