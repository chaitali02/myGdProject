TRUNCATE TABLE fact_transaction_journal;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/aml/data/csv/noheader/fact_transaction_journal.csv' IGNORE INTO TABLE fact_transaction_journal FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' ;

TRUNCATE TABLE customer_alert_summary;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/aml/data/csv/noheader/customer_alert_summary.csv' IGNORE INTO TABLE customer_alert_summary FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' ;


TRUNCATE TABLE rule_alert_summary;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/aml/data/csv/noheader/rule_alert_summary.csv' IGNORE INTO TABLE rule_alert_summary FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' ;

TRUNCATE TABLE country_alert_summary;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/aml/data/csv/noheader/country_alert_summary.csv' IGNORE INTO TABLE country_alert_summary FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r' ;

TRUNCATE TABLE dim_country;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/dim_country.csv' IGNORE INTO TABLE dim_country FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

