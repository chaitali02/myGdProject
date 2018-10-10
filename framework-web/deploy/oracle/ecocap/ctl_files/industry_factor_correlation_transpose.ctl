OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/industry_factor_correlation_transpose.csv'
TRUNCATE
INTO TABLE industry_factor_correlation_transpose
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
 (FACTOR_X,REPORTING_DATE,FACTOR_Y,FACTOR_VALUE) 
