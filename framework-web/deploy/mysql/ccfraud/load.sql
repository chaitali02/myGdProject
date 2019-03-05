truncate table cc_transactions;
load data local infile '/user/hive/warehouse/framework/app/ccfraud/data/csv/noheader/cc_transactions.csv' ignore into table cc_transactions fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n' ;

