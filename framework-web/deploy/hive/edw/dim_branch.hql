DROP TABLE IF EXISTS dim_branch;
CREATE TABLE IF NOT EXISTS `dim_branch`(
  `branch_id` string,
  `src_branch_id` string,
  `branch_type_code` string, 
  `branch_name` string, 
  `branch_desc` string, 
  `branch_contact_name` string, 
  `branch_contact_phone` string, 
  `branch_contact_email` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';