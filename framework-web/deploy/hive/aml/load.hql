truncate table FACT_TRANSACTION_JOURNAL

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/aml/data/csv/noheader/fact_transaction_journal.csv' IGNORE INTO TABLE FACT_TRANSACTION_JOURNAL FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' ;
