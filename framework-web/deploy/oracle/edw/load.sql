TRUNCATE TABLE DIM_ACCOUNT;
TRUNCATE TABLE DIM_ADDRESS;
TRUNCATE TABLE DIM_BANK;
TRUNCATE TABLE DIM_BRANCH; 
TRUNCATE TABLE DIM_COUNTRY;
TRUNCATE TABLE DIM_CUSTOMER;
TRUNCATE TABLE DIM_STATE;
TRUNCATE TABLE DIM_TRANSACTION_TYPE;
TRUNCATE TABLE DP_RULE_RESULTS;
TRUNCATE TABLE DQ_RULE_RESULTS;
TRUNCATE TABLE FACT_ACCOUNT_SUMMARY_MONTHLY;
TRUNCATE TABLE FACT_CUSTOMER_SUMMARY_MONTHLY;
TRUNCATE TABLE FACT_TRANSACTION;
TRUNCATE TABLE PROFILE_RULE_RESULTS;
TRUNCATE TABLE RC_RULE_RESULTS;


sqlldr edw_small/edw_small /data/controlFilesOracle/ACCOUNT.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/ACCOUNT_STATUS_TYPE.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/ACCOUNT_TYPE.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/ADDRESS.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/BANK.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/BRANCH.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/BRANCH_TYPE.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/CUSTOMER.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/DIM_DATE.ctl
sqlldr edw_small/edw_small /data/controlFilesOracle/PRODUCT_TYPE.ctl





