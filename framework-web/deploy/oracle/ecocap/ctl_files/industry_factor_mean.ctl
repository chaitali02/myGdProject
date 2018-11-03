 OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_mean.csv'
TRUNCATE
INTO TABLE industry_factor_mean
FIELDS TERMINATED BY ','
TRAILING NULLCOLS
 (	ID , 
	MEAN , 
	REPORTING_DATE
	)
