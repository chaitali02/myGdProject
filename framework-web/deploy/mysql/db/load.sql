truncate table account_summary_daily;
load data local infile '/user/hive/warehouse/framework/app/aml/data/csv/noheader/account_summary_daily.csv' ignore into table account_summary_daily fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ;


