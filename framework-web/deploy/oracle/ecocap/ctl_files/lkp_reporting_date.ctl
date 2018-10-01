OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/upload/lkp_reporting_date.csv'
TRUNCATE
INTO TABLE lkp_reporting_date
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
 (	REPORTING_DATE 
   ) 
