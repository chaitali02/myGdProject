CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS portfolio_var_heatmap_buckets;
CREATE  TABLE IF NOT EXISTS `portfolio_var_heatmap_buckets`(
  `portfolio_pd_bucket` string, 
  `portfolio_lgd_bucket` string, 
  `version` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';