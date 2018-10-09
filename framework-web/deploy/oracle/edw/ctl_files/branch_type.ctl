OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch_type.csv'
TRUNCATE
INTO TABLE BRANCH_TYPE
FIELDS TERMINATED BY ','
TRAILING NULLCOLS 
(	BRANCH_TYPE_ID ,
	BRANCH_TYPE_CODE , 
	BRANCH_TYPE_DESC , 
	LOAD_DATE , 
	LOAD_ID 
   ) 
