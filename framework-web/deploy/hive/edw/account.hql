DROP TABLE IF EXISTS account;
CREATE TABLE IF NOT EXISTS `account`(
  `account_id` string, 
  `account_type_id` int, 
  `account_status_id` int, 
  `product_type_id` int, 
  `customer_id` string, 
  `pin_number` int, 
  `nationality` string, 
  `primary_iden_doc` string, 
  `primary_iden_doc_id` string, 
  `secondary_iden_doc` string, 
  `secondary_iden_doc_id` string, 
  `account_open_date` string, 
  `account_number` string, 
  `opening_balance` string, 
  `current_balance` string, 
  `overdue_balance` int, 
  `overdue_date` string, 
  `currency_code` string, 
  `interest_type` string, 
  `interest_rate` float)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';