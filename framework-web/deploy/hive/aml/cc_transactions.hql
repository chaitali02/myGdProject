CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS cc_transactions;
CREATE EXTERNAL TABLE IF NOT EXISTS `cc_transactions`(
    `time` int, 
    `V1` double,
	`V2` double,
	`V3` double,
	`V4` double,
	`V5` double,
	`V6` double,
	`V7` double,
	`V8` double,
	`V9` double,
	`V10` double,
	`V11` double,
	`V12` double,
	`V13` double,
	`V14` double,
	`V15` double,
	`V16` double,
	`V17` double,
	`V18` double,
	`V19` double,
	`V20` double,
	`V21` double,
	`V22` double,
	`V23` double,
	`V24` double,
	`V25` double,
	`V26` double,
	`V27` double,
	`V28` double,	  
    `amount` double, 
    `class` int,  
    `version` int)
     PARTITIONED BY ( 
    `load_date` string,
    `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.cc_transactions ADD PARTITION(load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/cc_transactions.csv' OVERWRITE INTO TABLE cc_transactions PARTITION (load_date='2017-07-01', load_id='00');



