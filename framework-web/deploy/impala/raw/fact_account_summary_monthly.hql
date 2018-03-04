CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS fact_account_summary_monthly;
CREATE  TABLE IF NOT EXISTS `fact_account_summary_monthly`(
  `account_id` string, 
  `yyyy_mm` string, 
  `total_trans_count` bigint, 
  `total_trans_amount_usd` DECIMAL(38,2), 
  `avg_trans_amount` DECIMAL(38,2), 
  `min_amount` DECIMAL(38,2), 
  `max_amount` DECIMAL(38,2))
PARTITIONED BY ( 
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE framework.fact_account_summary_monthly ADD PARTITION(load_date='2017-07-01', load_id=00);
