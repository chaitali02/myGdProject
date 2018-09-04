CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS fact_customer_summary_monthly;
CREATE TABLE IF NOT EXISTS `fact_customer_summary_monthly`(
  `customer_id` string, 
  `yyyy_mm` string, 
  `total_trans_count` int, 
  `total_trans_amount_usd` int, 
  `avg_trans_amount` int, 
  `min_amount` int, 
  `max_amount` int)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

ALTER TABLE edw_small.fact_customer_summary_monthly ADD PARTITION(load_date='2017-12-04', load_id='00');



