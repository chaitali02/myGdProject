DROP TABLE IF EXISTS product_type;
CREATE TABLE IF NOT EXISTS `product_type`(
  `product_type_id` string, 
  `product_type_code` string, 
  `product_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';