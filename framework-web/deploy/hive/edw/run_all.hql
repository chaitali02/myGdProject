DROP TABLE IF EXISTS account;
CREATE TABLE IF NOT EXISTS `account`(
  `account_id` string, 
  `account_type_id` string, 
  `account_status_id` string, 
  `product_type_id` string, 
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS account_status_type;
CREATE TABLE IF NOT EXISTS `account_status_type`(
  `account_status_id` string, 
  `account_status_code` string, 
  `account_status_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

  
DROP TABLE IF EXISTS account_type;
CREATE TABLE IF NOT EXISTS `account_type`(
  `account_type_id` string, 
  `account_type_code` string, 
  `account_type_desc` string)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS address;
CREATE TABLE IF NOT EXISTS `address`(
  `address_id` string, 
  `address_line1` string, 
  `address_line2` string, 
  `address_line3` string, 
  `city` string, 
  `county` string, 
  `state` string, 
  `zipcode` int, 
  `country` string, 
  `latitude` string, 
  `longitude` string)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
DROP TABLE IF EXISTS bank;
CREATE TABLE IF NOT EXISTS `bank`(
  `bank_id` string, 
  `bank_code` string, 
  `bank_name` string, 
  `bank_account_number` string, 
  `bank_currency_code` string, 
  `bank_check_digits` int)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS branch;
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS branch_type;
CREATE TABLE IF NOT EXISTS `branch_type`(
  `branch_type_id` string, 
  `branch_type_code` string, 
  `branch_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS customer;
CREATE  TABLE IF NOT EXISTS `customer`(
  `customer_id` string, 
  `address_id` string, 
  `branch_id` int, 
  `title` string, 
  `first_name` string, 
  `middle_name` string, 
  `last_name` string, 
  `ssn` string, 
  `phone` string, 
  `date_first_purchase` string, 
  `commute_distance_miles` int)
  PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_account;
CREATE  TABLE IF NOT EXISTS `dim_account`(
  `account_id` string, 
  `src_account_id` string,
  `account_type_code` string, 
  `account_status_code` string, 
  `product_type_code` string, 
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
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_address;
CREATE TABLE IF NOT EXISTS `dim_address`(
  `address_id` string, 
  `src_address_id` string,
  `address_line1` string, 
  `address_line2` string, 
  `address_line3` string, 
  `city` string, 
  `county` string, 
  `state` string, 
  `zipcode` int, 
  `country` string, 
  `latitude` string, 
  `longtitude` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_bank;
CREATE TABLE IF NOT EXISTS `dim_bank`(
  `bank_id` string,
  `src_bank_id` string,
  `bank_code` string, 
  `bank_name` string, 
  `bank_account_number` string, 
  `bank_currency_code` string, 
  `bank_check_digits` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_branch;
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_country;
CREATE TABLE IF NOT EXISTS `dim_country`(
  `country_code` string,
  `country_name` string,
  `country_population` int, 
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_customer;
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_date;
CREATE TABLE IF NOT EXISTS `dim_date`(
  `date_id` int, 
  `date_type` string,
  `date_val` string,
  `day_num_of_week` int, 
  `day_num_of_month` int, 
  `day_num_of_quarter` int, 
  `day_num_of_year` int, 
  `day_num_absolute` int, 
  `day_of_week_name` string, 
  `day_of_week_abbreviation` string, 
  `julian_day_num_of_year` int, 
  `julian_day_num_absolute` int, 
  `is_weekday` string, 
  `is_usa_civil_holiday` string, 
  `is_last_day_of_week` string, 
  `is_last_day_of_month` string, 
  `is_last_day_of_quarter` string, 
  `is_last_day_of_year` string, 
  `is_last_day_of_fiscal_month` string, 
  `is_last_day_of_fiscal_quarter` string, 
  `is_last_day_of_fiscal_year` string, 
  `week_of_year_begin_date` string, 
  `week_of_year_begin_date_key` int, 
  `week_of_year_end_date` string, 
  `week_of_year_end_date_key` int, 
  `week_of_month_begin_date` string, 
  `week_of_month_begin_date_key` int, 
  `week_of_month_end_date` string, 
  `week_of_month_end_date_key` int, 
  `week_of_quarter_begin_date` string, 
  `week_of_quarter_begin_date_key` int, 
  `week_of_quarter_end_date` string, 
  `week_of_quarter_end_date_key` int, 
  `week_num_of_month` int, 
  `week_num_of_quarter` int, 
  `week_num_of_year` int, 
  `month_num_of_year` int, 
  `month_num_overall` string,
  `month_name` string, 
  `month_name_abbreviation` string, 
  `month_begin_date` string, 
  `month_begin_date_key` int, 
  `month_end_date` string, 
  `month_end_date_key` int, 
  `quarter_num_of_year` int, 
  `quarter_num_overall` int, 
  `quarter_begin_date` string, 
  `quarter_begin_date_key` int, 
  `quarter_end_date` string, 
  `quarter_end_date_key` int, 
  `year_num` int, 
  `year_begin_date` string, 
  `year_begin_date_key` int, 
  `year_end_date` string, 
  `year_end_date_key` int, 
  `yyyy_mm` string, 
  `yyyy_mm_dd` string,
  `dd_mon_yyyy` string)
   PARTITIONED BY ( 
  `load_date` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_state;
CREATE TABLE IF NOT EXISTS `dim_state`(
  `state_code` string,
  `state_name` string,
  `country_code` string, 
  `state_population` int,
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dim_transaction_type;
CREATE TABLE IF NOT EXISTS `dim_transaction_type`(
  `transaction_type_id` string,
  `src_transaction_type_id` string,
  `transaction_type_code` string, 
  `transaction_type_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS dp_rule_results;
CREATE TABLE dp_rule_results
(
  `datapoduuid` string,
  `datapodversion` string,
  `datapodname` string,
  `attributeid` string,
  `attributename` string,
  `numrows` string,
  `minval` BIGINT,
  `maxval` BIGINT,
  `avgval` BIGINT,
  `medianval` BIGINT,
  `stddev` BIGINT,
  `numdistinct` int,
  `perdistinct` BIGINT,
  `numnull` int,
  `pernull` BIGINT,
  `sixsigma` BIGINT,
  `load_date` string,
  `load_id` int,
  `version` int
);DROP TABLE IF EXISTS dq_rule_results;
CREATE TABLE IF NOT EXISTS `dq_rule_results`(
  `rowkey` string,
  `datapoduuid` string,
  `datapodversion` string,
  `datapodname` string,
  `attributeid` string,
  `attributename` string,  
  `attributevalue` string,
  `nullcheck_pass` string,
  `valuecheck_pass` string,
  `rangecheck_pass` string,
  `datatypecheck_pass` string,
  `dataformatcheck_pass` string,
  `lengthcheck_pass` string,
  `refintegritycheck_pass` string,
  `dupcheck_pass` string,
  `customcheck_pass` string,
  `version` int);
DROP TABLE IF EXISTS fact_account_summary_monthly;
CREATE TABLE IF NOT EXISTS `fact_account_summary_monthly`(
  `account_id` string, 
  `yyyy_mm` string, 
  `total_trans_count` bigint, 
  `total_trans_amount_usd` DECIMAL(38,2), 
  `avg_trans_amount` DECIMAL(38,2), 
  `min_amount` DECIMAL(38,2), 
  `max_amount` DECIMAL(38,2))
PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS fact_customer_summary_monthly;
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS fact_transaction;
CREATE  TABLE IF NOT EXISTS `fact_transaction`(
  `transaction_id` string,
  `src_transaction_id` string,
  `transaction_type_id` string, 
  `trans_date_id` string, 
  `bank_id` string, 
  `branch_id` string, 
  `customer_id` string, 
  `address_id` string, 
  `account_id` string, 
  `from_account` string, 
  `to_account` string, 
  `amount_base_curr` int, 
  `amount_usd` int, 
  `currency_code` string, 
  `currency_rate` bigint, 
  `notes` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS model_training_set;

CREATE TABLE IF NOT EXISTS model_training_set
(
  customer_id int,
  address_id int,
  branch_id int,
  commute_distance_miles int,
  label int,
  censor int,
  version int
);

DROP TABLE IF EXISTS product_type;
CREATE TABLE IF NOT EXISTS `product_type`(
  `product_type_id` string, 
  `product_type_code` string, 
  `product_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS rc_rule_results;
CREATE TABLE rc_rule_results (
    `sourcedatapoduuid` string,
    `sourcedatapodversion` string,
    `sourcedatapodname` string,
    `sourceattributeid` string,
    `sourceattributename` string,
    `sourcevalue` BIGINT,
    `targetdatapoduuid` string,
    `targetdatapodversion` string,
    `targetdatapodname` string,
    `targetattributeid` string,
    `targetattributename` string,
    `targetvalue` BIGINT,
    `status` string,
    `version` int
);DROP TABLE IF EXISTS transaction;
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS transaction_type;
CREATE TABLE IF NOT EXISTS `transaction_type`(
  `transaction_type_id` string, 
  `transaction_type_code` string, 
  `transaction_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';