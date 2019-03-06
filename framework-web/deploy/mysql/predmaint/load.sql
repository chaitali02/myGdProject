truncate table fact_transaction_journal;
load data local infile '/user/hive/warehouse/framework/app/aml/data/csv/noheader/fact_transaction_journal2.csv' ignore into table fact_transaction_journal fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ;

truncate table customer_alert_summary;
load data local infile '/user/hive/warehouse/framework/app/aml/data/csv/noheader/customer_alert_summary.csv' ignore into table customer_alert_summary fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ;

truncate table rule_alert_summary;
load data local infile '/user/hive/warehouse/framework/app/aml/data/csv/noheader/rule_alert_summary.csv' ignore into table rule_alert_summary fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ;

truncate table country_alert_summary;
load data local infile '/user/hive/warehouse/framework/app/aml/data/csv/noheader/country_alert_summary.csv' ignore into table country_alert_summary fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ;

truncate table dim_country;
load data local infile '/user/hive/warehouse/framework/app/edw/data/csv/header/dim_country.csv' ignore into table dim_country fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ignore 1 lines;