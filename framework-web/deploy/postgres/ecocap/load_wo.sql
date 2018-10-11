TRUNCATE TABLE CUSTOMER_IDIOSYNCRATIC_RISK;
TRUNCATE TABLE CUSTOMER_PORTFOLIO;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION; 
TRUNCATE TABLE INDUSTRY_FACTOR_MEAN;
TRUNCATE TABLE INDUSTRY_FACTOR_SIMULATION;
TRUNCATE TABLE LKP_REPORTING_DATE;
TRUNCATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

Copy CUSTOMER_IDIOSYNCRATIC_RISK FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/customer_idiosyncratic_risk.csv' With csv delimiter ',';

Copy CUSTOMER_PORTFOLIO FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/customer_portfolio.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION_TRANSPOSE FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_correlation_transpose.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_correlation.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_MEAN FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_mean.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_SIMULATION FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_simulation.csv' With csv delimiter ',';

Copy LKP_REPORTING_DATE (REPORTING_DATE) FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/lkp_reporting_date.csv' delimiter ',';

Copy PORTFOLIO_VAR_HEATMAP_BUCKETS (PORTFOLIO_PD_BUCKET, PORTFOLIO_LGD_BUCKET) FROM '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/portfolio_var_heatmap_buckets.csv' delimiter ',';


