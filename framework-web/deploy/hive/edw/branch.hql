DROP TABLE IF EXISTS branch;
CREATE TABLE IF NOT EXISTS `branch`(
  `branch_id` string, 
  `branch_type_id` string, 
  `bank_id` string, 
  `address_id` int, 
  `branch_name` string, 
  `branch_desc` string, 
  `branch_contact_name` string, 
  `branch_contact_phone` string, 
  `branch_contact_email` string)
  PARTITIONED BY (
  `load_date` string,
  `load_id` BIGINT)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';