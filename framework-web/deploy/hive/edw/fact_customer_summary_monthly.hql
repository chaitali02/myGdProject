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