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