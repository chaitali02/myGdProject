DROP TABLE IF EXISTS branch_type;
CREATE TABLE IF NOT EXISTS `branch_type`(
  `branch_type_id` string, 
  `branch_type_code` string, 
  `branch_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';