DROP TABLE IF EXISTS dim_customer;
CREATE TABLE IF NOT EXISTS `dim_customer`(
  `customer_id` string, 
  `src_customer_id` string,
  `title` string, 
  `first_name` string, 
  `middle_name` string, 
  `last_name` string, 
  `address_line1` string, 
  `address_line2` string, 
  `phone` string, 
  `date_first_purchase` string, 
  `commute_distance` int, 
  `city` string, 
  `state` string, 
  `postal_code` int, 
  `country` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';