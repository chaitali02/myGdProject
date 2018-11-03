CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS cc_transactions_predictions;
CREATE EXTERNAL TABLE IF NOT EXISTS `cc_transactions_predictions`(
    `label` double, 
    `features` string,
	`rawPrediction` string,
	`probability` string,
	`prediction` double)
     PARTITIONED BY ( 
    `load_date` string,
    `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.cc_transactions_predictions ADD PARTITION(load_date='2017-07-01', load_id='00');



