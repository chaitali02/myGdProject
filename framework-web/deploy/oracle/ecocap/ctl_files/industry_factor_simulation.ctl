OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/industry_factor_simulation.csv'
TRUNCATE
INTO TABLE industry_factor_simulation
FIELDS TERMINATED BY ','
TRAILING NULLCOLS 
(	
	ITERATION_ID,
	FACTOR1,
	FACTOR2,
	FACTOR3,
	FACTOR4,
	REPORTING_DATE
   ) 
