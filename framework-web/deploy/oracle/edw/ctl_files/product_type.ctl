OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/product_type.csv'
TRUNCATE
INTO TABLE PRODUCT_TYPE
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
   (	PRODUCT_TYPE_ID ,
	PRODUCT_TYPE_CODE , 
	PRODUCT_TYPE_DESC , 
	LOAD_DATE , 
	LOAD_ID 
   ) 
