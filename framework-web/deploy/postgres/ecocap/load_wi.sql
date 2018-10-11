TRUNCATE TABLE CUSTOMER_PORTFOLIO;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION;
TRUNCATE TABLE INDUSTRY_FACTOR_MEAN; 
TRUNCATE TABLE LKP_REPORTING_DATE;
TRUNCATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

Copy CUSTOMER_PORTFOLIO FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION_TRANSPOSE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_MEAN FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy LKP_REPORTING_DATE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy PORTFOLIO_VAR_HEATMAP_BUCKETS FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

