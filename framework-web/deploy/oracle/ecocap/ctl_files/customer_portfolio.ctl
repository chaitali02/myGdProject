OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/upload/customer_portfolio.csv'
TRUNCATE 
INTO TABLE customer_portfolio
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
(CUST_ID,INDUSTRY,PD,EXPOSURE,LGD,LGD_VAR,CORRELATION,SQRT_CORRELATION,DEF_POINT,REPORTING_DATE) 
