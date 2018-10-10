OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/industry_factor_correlation.csv'
TRUNCATE
INTO TABLE industry_factor_correlation
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
(FACTOR,FACTOR1,FACTOR2,FACTOR3,FACTOR4,REPORTING_DATE)
 
