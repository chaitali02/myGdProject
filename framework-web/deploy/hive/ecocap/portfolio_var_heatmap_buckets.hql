CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table portfolio_var_heatmap_buckets set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS portfolio_var_heatmap_buckets;

CREATE  TABLE IF NOT EXISTS `portfolio_var_heatmap_buckets`(
  `portfolio_pd_bucket` string, 
  `portfolio_lgd_bucket` string, 
  `version` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/portfolio_var_heatmap_buckets.csv' OVERWRITE INTO TABLE ecocap.portfolio_var_heatmap_buckets;


