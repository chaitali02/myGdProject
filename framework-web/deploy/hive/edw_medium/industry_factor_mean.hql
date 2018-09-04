CREATE DATABASE IF NOT EXISTS `edw_medium`;
USE `edw_medium`;
DROP TABLE IF EXISTS `industry_factor_mean`;
CREATE TABLE `industry_factor_mean` (
    `id` string,
    `mean` double,
    `reporting_date` string,
    `version` int
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
