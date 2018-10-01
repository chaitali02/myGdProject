
truncate table dim_account;
truncate table dim_address;
truncate table dim_bank;
truncate table dim_branch; 
truncate table dim_country;
truncate table dim_customer;
truncate table dim_state;
truncate table dim_transaction_type;
truncate table dp_rule_results;
truncate table dq_rule_results;
truncate table fact_account_summary_monthly;
truncate table fact_customer_summary_monthly;
truncate table fact_transaction;
truncate table profile_rule_results;
truncate table rc_rule_results;



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





