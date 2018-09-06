DROP TABLE IF EXISTS transaction;
CREATE TABLE IF NOT EXISTS `transaction`(
  `transaction_id` string, 
  `transaction_type_id` string, 
  `account_id` string, 
  `transaction_date` string, 
  `from_account` string, 
  `to_account` string, 
  `amount_base_curr` decimal(30,2), 
  `amount_usd` decimal(30,2), 
  `currency_code` string, 
  `currency_rate` float, 
  `notes` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';