 
OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv'
TRUNCATE
INTO TABLE ACCOUNT_STATUS_TYPE
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
(	ACCOUNT_STATUS_ID ,
	ACCOUNT_STATUS_CODE , 
	ACCOUNT_STATUS_DESC , 
	LOAD_DATE , 
	LOAD_ID 
   ) 
