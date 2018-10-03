
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `account_id` varchar(45) NOT NULL DEFAULT '',
  `account_type_id` int(11) DEFAULT NULL,
  `account_status_id` int(11) DEFAULT NULL,
  `product_type_id` int(11) DEFAULT NULL,
  `customer_id` varchar(45) DEFAULT NULL,
  `pin_number` int(11) DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  `primary_iden_doc` varchar(45) DEFAULT NULL,
  `primary_iden_doc_id` varchar(45) DEFAULT NULL,
  `secondary_iden_doc` varchar(45) DEFAULT NULL,
  `secondary_iden_doc_id` varchar(45) DEFAULT NULL,
  `account_open_date` varchar(45) DEFAULT NULL,
  `account_number` varchar(45) DEFAULT NULL,
  `opening_balance` varchar(45) DEFAULT NULL,
  `current_balance` varchar(45) DEFAULT NULL,
  `overdue_balance` int(11) DEFAULT NULL,
  `overdue_date` varchar(45) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `interest_type` varchar(45) DEFAULT NULL,
  `interest_rate` float DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `account_status_type`;
CREATE TABLE `account_status_type` (
  `account_status_id` int(11) NOT NULL DEFAULT '0',
  `account_status_code` varchar(45) DEFAULT NULL,
  `account_status_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_status_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `account_type`;
CREATE TABLE `account_type` (
  `account_type_id` int(11) NOT NULL DEFAULT '0',
  `account_type_code` varchar(45) DEFAULT NULL,
  `account_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_type_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_id` varchar(45) NOT NULL DEFAULT '',
  `address_line1` varchar(45) DEFAULT NULL,
  `address_line2` varchar(45) DEFAULT NULL,
  `address_line3` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `county` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `zipcode` int(11) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `latitude` varchar(45) DEFAULT NULL,
  `longitude` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`address_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `bank`;
CREATE TABLE `bank` (
  `bank_id` int(11) NOT NULL DEFAULT '0',
  `bank_code` varchar(45) DEFAULT NULL,
  `bank_name` varchar(45) DEFAULT NULL,
  `bank_account_number` varchar(45) DEFAULT NULL,
  `bank_currency_code` varchar(45) DEFAULT NULL,
  `bank_check_digits` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bank_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `branch`;
CREATE TABLE `branch` (
  `branch_id` int(11) NOT NULL DEFAULT '0',
  `branch_type_id` int(11) DEFAULT NULL,
  `bank_id` varchar(45) DEFAULT NULL,
  `address_id` varchar(45) DEFAULT NULL,
  `branch_name` varchar(45) DEFAULT NULL,
  `branch_desc` varchar(45) DEFAULT NULL,
  `branch_contact_name` varchar(45) DEFAULT NULL,
  `branch_contact_phone` varchar(45) DEFAULT NULL,
  `branch_contact_email` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `branch_type`;
CREATE TABLE `branch_type` (
  `branch_type_id` int(11) NOT NULL DEFAULT '0',
  `branch_type_code` varchar(45) DEFAULT NULL,
  `branch_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_type_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `customer_id` varchar(45) NOT NULL DEFAULT '',
  `address_id` varchar(45) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ssn` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `date_first_purchase` varchar(45) DEFAULT NULL,
  `commute_distance_miles` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `dim_account`;
CREATE TABLE `dim_account` (
  `account_id` varchar(45) NOT NULL DEFAULT '',
  `src_account_id` varchar(45) DEFAULT NULL,
  `account_type_code` varchar(45) DEFAULT NULL,
  `account_status_code` varchar(45) DEFAULT NULL,
  `product_type_code` varchar(45) DEFAULT NULL,
  `pin_number` int(11) DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  `primary_iden_doc` varchar(45) DEFAULT NULL,
  `primary_iden_doc_id` varchar(45) DEFAULT NULL,
  `secondary_iden_doc` varchar(45) DEFAULT NULL,
  `secondary_iden_doc_id` varchar(45) DEFAULT NULL,
  `account_open_date` varchar(45) DEFAULT NULL,
  `account_number` varchar(45) DEFAULT NULL,
  `opening_balance` varchar(45) DEFAULT NULL,
  `current_balance` varchar(45) DEFAULT NULL,
  `overdue_balance` int(11) DEFAULT NULL,
  `overdue_date` varchar(45) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `interest_type` varchar(45) DEFAULT NULL,
  `interest_rate` float DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_account_id` (`src_account_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `dim_address`;
CREATE TABLE `dim_address` (
  `address_id` varchar(45) NOT NULL DEFAULT '',
  `src_address_id` varchar(45) DEFAULT NULL,
  `address_line1` varchar(45) DEFAULT NULL,
  `address_line2` varchar(45) DEFAULT NULL,
  `address_line3` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `county` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `zipcode` int(11) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `latitude` varchar(45) DEFAULT NULL,
  `longtitude` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`address_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_address_id` (`src_address_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `dim_bank`;
CREATE TABLE `dim_bank` (
  `bank_id` varchar(45) NOT NULL DEFAULT '',
  `src_bank_id` varchar(45) DEFAULT NULL,
  `bank_code` varchar(45) DEFAULT NULL,
  `bank_name` varchar(45) DEFAULT NULL,
  `bank_account_number` varchar(45) DEFAULT NULL,
  `bank_currency_code` varchar(45) DEFAULT NULL,
  `bank_check_digits` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bank_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_bank_id` (`src_bank_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `dim_branch`;
CREATE TABLE `dim_branch` (
  `branch_id` varchar(45) NOT NULL DEFAULT '',
  `src_branch_id` varchar(45) DEFAULT NULL,
  `branch_type_code` varchar(45) DEFAULT NULL,
  `branch_name` varchar(45) DEFAULT NULL,
  `branch_desc` varchar(45) DEFAULT NULL,
  `branch_contact_name` varchar(45) DEFAULT NULL,
  `branch_contact_phone` varchar(45) DEFAULT NULL,
  `branch_contact_email` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`branch_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_branch_id` (`src_branch_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS dim_country;
CREATE TABLE IF NOT EXISTS `dim_country`(
  `country_code` varchar(45) NOT NULL DEFAULT '',
  `country_name` varchar(45) NOT NULL DEFAULT '',
  `country_population` int(10) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`country_code`,`load_date`,`load_id`)
  );
DROP TABLE IF EXISTS `dim_customer`;
CREATE TABLE `dim_customer` (
  `customer_id` varchar(45) NOT NULL DEFAULT '',
  `src_customer_id` varchar(45) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `address_line1` varchar(45) DEFAULT NULL,
  `address_line2` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `date_first_purchase` varchar(45) DEFAULT NULL,
  `commute_distance` int(11) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `postal_code` varchar(45) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_customer_id` (`src_customer_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `dim_date`;
CREATE TABLE `dim_date` (
  `date_id` int(11) NOT NULL DEFAULT '0',
  `date_type` varchar(45) DEFAULT NULL,
  `date_val` varchar(45) DEFAULT NULL,
  `day_num_of_week` int(11) DEFAULT NULL,
  `day_num_of_month` int(11) DEFAULT NULL,
  `day_num_of_quarter` int(11) DEFAULT NULL,
  `day_num_of_year` int(11) DEFAULT NULL,
  `day_num_absolute` int(11) DEFAULT NULL,
  `day_of_week_name` varchar(45) DEFAULT NULL,
  `day_of_week_abbreviation` varchar(45) DEFAULT NULL,
  `julian_day_num_of_year` int(11) DEFAULT NULL,
  `julian_day_num_absolute` int(11) DEFAULT NULL,
  `is_weekday` varchar(45) DEFAULT NULL,
  `is_usa_civil_holiday` varchar(45) DEFAULT NULL,
  `is_last_day_of_week` varchar(45) DEFAULT NULL,
  `is_last_day_of_month` varchar(45) DEFAULT NULL,
  `is_last_day_of_quarter` varchar(45) DEFAULT NULL,
  `is_last_day_of_year` varchar(45) DEFAULT NULL,
  `is_last_day_of_fiscal_month` varchar(45) DEFAULT NULL,
  `is_last_day_of_fiscal_quarter` varchar(45) DEFAULT NULL,
  `is_last_day_of_fiscal_year` varchar(45) DEFAULT NULL,
  `week_of_year_begin_date` varchar(45) DEFAULT NULL,
  `week_of_year_begin_date_key` int(11) DEFAULT NULL,
  `week_of_year_end_date` varchar(45) DEFAULT NULL,
  `week_of_year_end_date_key` int(11) DEFAULT NULL,
  `week_of_month_begin_date` varchar(45) DEFAULT NULL,
  `week_of_month_begin_date_key` int(11) DEFAULT NULL,
  `week_of_month_end_date` varchar(45) DEFAULT NULL,
  `week_of_month_end_date_key` int(11) DEFAULT NULL,
  `week_of_quarter_begin_date` varchar(45) DEFAULT NULL,
  `week_of_quarter_begin_date_key` int(11) DEFAULT NULL,
  `week_of_quarter_end_date` varchar(45) DEFAULT NULL,
  `week_of_quarter_end_date_key` int(11) DEFAULT NULL,
  `week_num_of_month` int(11) DEFAULT NULL,
  `week_num_of_quarter` int(11) DEFAULT NULL,
  `week_num_of_year` int(11) DEFAULT NULL,
  `month_num_of_year` int(11) DEFAULT NULL,
  `month_num_overall` varchar(45) DEFAULT NULL,
  `month_name` varchar(45) DEFAULT NULL,
  `month_name_abbreviation` varchar(45) DEFAULT NULL,
  `month_begin_date` varchar(45) DEFAULT NULL,
  `month_begin_date_key` int(11) DEFAULT NULL,
  `month_end_date` varchar(45) DEFAULT NULL,
  `month_end_date_key` int(11) DEFAULT NULL,
  `quarter_num_of_year` int(11) DEFAULT NULL,
  `quarter_num_overall` int(11) DEFAULT NULL,
  `quarter_begin_date` varchar(45) DEFAULT NULL,
  `quarter_begin_date_key` int(11) DEFAULT NULL,
  `quarter_end_date` varchar(45) DEFAULT NULL,
  `quarter_end_date_key` int(11) DEFAULT NULL,
  `year_num` int(11) DEFAULT NULL,
  `year_begin_date` varchar(45) DEFAULT NULL,
  `year_begin_date_key` int(11) DEFAULT NULL,
  `year_end_date` varchar(45) DEFAULT NULL,
  `year_end_date_key` int(11) DEFAULT NULL,
  `yyyy_mm` varchar(45) DEFAULT NULL,
  `yyyy_mm_dd` varchar(45) DEFAULT NULL,
  `dd_mon_yyyy` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',  
  PRIMARY KEY (`date_id`,`load_date`,`load_id`),
  UNIQUE KEY `date_val` (`date_val`,`load_date`)
);
DROP TABLE IF EXISTS dim_state;
CREATE TABLE IF NOT EXISTS `dim_state`(
  `state_code` varchar(45) NOT NULL DEFAULT '',
  `state_name` varchar(45) NOT NULL DEFAULT '',
  `country_code` varchar(45) NOT NULL DEFAULT '', 
  `state_population` int(10) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`state_code`,`load_date`,`load_id`)
  );
DROP TABLE IF EXISTS `dim_transaction_type`;
CREATE TABLE `dim_transaction_type` (
  `transaction_type_id` varchar(45) NOT NULL DEFAULT '',
  `src_transaction_type_id` int(11) DEFAULT NULL,
  `transaction_type_code` varchar(45) DEFAULT NULL,
  `transaction_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`transaction_type_id`,`load_date`,`load_id`),
  UNIQUE KEY `src_transaction_type_id` (`src_transaction_type_id`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `dp_rule_results`;
CREATE TABLE `dp_rule_results` (
    `datapoduuid` varchar(45) DEFAULT NULL,
    `datapodversion` varchar(45) DEFAULT NULL,
    `datapodname` varchar(45) DEFAULT NULL,
    `attributeid` varchar(45) DEFAULT NULL,
    `attributename` varchar(45) DEFAULT NULL,
    `numrows` varchar(45) DEFAULT NULL,
    `minval` double precision,
    `maxval` double precision,
    `avgval` double precision,
    `medianval` double precision,
    `stddev` double precision,
    `numdistinct` int(11) DEFAULT NULL,
    `perdistinct` double precision,
    `numnull` int(11) DEFAULT NULL,
    `pernull` double precision,
    `sixsigma` double precision,
    `load_date` VARCHAR(45) DEFAULT NULL,
    `load_id` int(11) DEFAULT NULL,
    `version` int(11) DEFAULT NULL
);
DROP TABLE IF EXISTS `dq_rule_results`;
CREATE TABLE `dq_rule_results` (
  `rowkey` varchar(45) DEFAULT NULL,
  `datapoduuid` varchar(45) DEFAULT NULL,
  `datapodversion` varchar(45) DEFAULT NULL,  
  `datapodname` varchar(45) DEFAULT NULL,
  `attributeid` varchar(45) DEFAULT NULL,
  `attributename` varchar(45) DEFAULT NULL,
  `attributevalue` varchar(45) DEFAULT NULL,
  `nullcheck_pass` varchar(45) DEFAULT NULL,
  `valuecheck_pass` varchar(45) DEFAULT NULL,
  `rangecheck_pass` varchar(45) DEFAULT NULL,
  `datatypecheck_pass` varchar(45) DEFAULT NULL,
  `dataformatcheck_pass` varchar(45) DEFAULT NULL,
  `lengthcheck_pass` varchar(45) DEFAULT NULL,
  `refintegritycheck_pass` varchar(45) DEFAULT NULL,
  `dupcheck_pass` varchar(45) DEFAULT NULL,
  `customcheck_pass` varchar(45) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);
DROP TABLE IF EXISTS `fact_account_summary_monthly`;
CREATE TABLE `fact_account_summary_monthly` (
  `account_id` varchar(45) NOT NULL DEFAULT '',
  `yyyy_mm` varchar(45) NOT NULL DEFAULT '',
  `total_trans_count` bigint(20) DEFAULT NULL,
  `total_trans_amount_usd` int(11) DEFAULT NULL,
  `avg_trans_amount` int(11) DEFAULT NULL,
  `min_amount` decimal(38,2) DEFAULT NULL,
  `max_amount` int(11) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`,`yyyy_mm`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `fact_customer_summary_monthly`;
CREATE TABLE `fact_customer_summary_monthly` (
  `customer_id` varchar(45) NOT NULL DEFAULT '',
  `yyyy_mm` varchar(45) NOT NULL DEFAULT '',
  `total_trans_count` varchar(45) DEFAULT NULL,
  `total_trans_amount_usd` int(11) DEFAULT NULL,
  `avg_trans_amount` int(11) DEFAULT NULL,
  `min_amount` decimal(38,2) DEFAULT NULL,
  `max_amount` decimal(38,2) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`customer_id`,`yyyy_mm`,`load_date`,`load_id`)
);
DROP TABLE IF EXISTS `fact_transaction`;
CREATE TABLE `fact_transaction` (
  `transaction_id` int(11) NOT NULL DEFAULT '0',
  `src_transaction_id` varchar(45) DEFAULT NULL,
  `transaction_type_id` int(11) DEFAULT NULL,
  `trans_date_id` int(11) DEFAULT NULL,
  `bank_id` int(11) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `customer_id` varchar(45) DEFAULT NULL,
  `address_id` varchar(45) DEFAULT NULL,
  `account_id` varchar(45) DEFAULT NULL,
  `from_account` varchar(45) DEFAULT NULL,
  `to_account` varchar(45) DEFAULT NULL,
  `amount_base_curr` int(11) DEFAULT NULL,
  `amount_usd` int(11) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `currency_rate` bigint(20) DEFAULT NULL,
  `notes` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0'
);
DROP TABLE IF EXISTS `model_training_set`;
CREATE TABLE model_training_set
(
  customer_id int(11) DEFAULT NULL,
  address_id int(11) DEFAULT NULL,
  branch_id int(11) DEFAULT NULL,
  commute_distance_miles int(11) DEFAULT NULL,
  label int(11) DEFAULT NULL,
  censor int(11) DEFAULT NULL,
  version int(11) DEFAULT NULL
);
DROP TABLE IF EXISTS `product_type`;
CREATE TABLE `product_type` (
  `product_type_id` int(11) NOT NULL DEFAULT '0',
  `product_type_code` varchar(45) DEFAULT NULL,
  `product_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`product_type_id`,`load_date`,`load_id`)  
);
DROP TABLE IF EXISTS `rc_rule_results`;
CREATE TABLE `rc_rule_results` (
    `sourcedatapoduuid` varchar(45) DEFAULT NULL,
    `sourcedatapodversion` varchar(45) DEFAULT NULL,
    `sourcedatapodname` varchar(45) DEFAULT NULL,
    `sourceattributeid` varchar(45) DEFAULT NULL,
    `sourceattributename` varchar(45) DEFAULT NULL,
    `sourcevalue` double DEFAULT NULL,
    `targetdatapoduuid` varchar(45) DEFAULT NULL,
    `targetdatapodversion` varchar(45) DEFAULT NULL,
    `targetdatapodname` varchar(45) DEFAULT NULL,
    `targetattributeid` varchar(45) DEFAULT NULL,
    `targetattributename` varchar(45) DEFAULT NULL,
    `targetvalue`  double DEFAULT NULL,
    `status` varchar(45) DEFAULT NULL,
    `version` int(11) DEFAULT NULL
);
DROP TABLE IF EXISTS `target_gen_data_uniform_dist`;
CREATE TABLE `target_gen_data_uniform_dist` (
  `id` int(11) DEFAULT NULL,
  `col1` double precision,
  `version` int(11) DEFAULT NULL
);
DROP TABLE IF EXISTS `target_sim_linear_regression`;
CREATE TABLE `target_sim_linear_regression` (
  `id` int(11) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `interest_rate` double precision,
  `account_type_id` double precision,
  `account_status_id` double precision
);
DROP TABLE IF EXISTS `target_sim_multivarient_normal_dis`;
CREATE TABLE `target_sim_multivarient_normal_dis` (
  `id` int(11) DEFAULT NULL,
  `interestRate` double precision,
  `col2` double precision,
  `col3` double precision,
  `version` int(11) DEFAULT NULL
);
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `transaction_id` varchar(45) NOT NULL DEFAULT '0',
  `transaction_type_id` int(11) DEFAULT NULL,
  `account_id` varchar(45) DEFAULT NULL,
  `transaction_date` varchar(45) DEFAULT NULL,
  `from_account` varchar(45) DEFAULT NULL,
  `to_account` varchar(45) DEFAULT NULL,
  `amount_base_curr` decimal(30,2) DEFAULT NULL,
  `amount_usd` decimal(30,2) DEFAULT NULL,
  `currency_code` varchar(45) DEFAULT NULL,
  `currency_rate` float DEFAULT NULL,
  `notes` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`transaction_id`,`load_date`,`load_id`)    
);
DROP TABLE IF EXISTS `transaction_type`;
CREATE TABLE `transaction_type` (
  `transaction_type_id` varchar(45) NOT NULL DEFAULT '0',
  `transaction_type_code` varchar(45) DEFAULT NULL,
  `transaction_type_desc` varchar(45) DEFAULT NULL,
  `load_date` varchar(45) NOT NULL DEFAULT '',
  `load_id` bigint(20) NOT NULL DEFAULT '0',
 PRIMARY KEY (`transaction_type_id`,`load_date`,`load_id`)  
);