OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/upload/portfolio_var_heatmap_buckets.csv'
TRUNCATE
INTO TABLE portfolio_var_heatmap_buckets
FIELDS TERMINATED BY ','
TRAILING NULLCOLS 
(	
	PORTFOLIO_PD_BUCKET,
    PORTFOLIO_LGD_BUCKET
   ) 
