truncate table customer_es_allocation;
truncate table customer_generate_data;
truncate table customer_idiosyncratic_risk;
truncate table customer_idiosyncratic_transpose_stage;
truncate table customer_idiosyncratic_transpose;
truncate table customer_loss_simulation;
truncate table customer_portfolio_clone;
truncate table customer_portfolio_ul_calc_allocation;
truncate table customer_portfolio_ul_calc_summary;
truncate table customer_portfolio_ul_calc;
truncate table customer_portfolio_ul;
truncate table customer_portfolio;
truncate table customer_var_contribution_topn_perc;
truncate table industry_factor_correlation_transpose;
truncate table industry_factor_correlation;
truncate table industry_factor_mean;
truncate table industry_factor_simulation_stage;
truncate table industry_factor_simulation;
truncate table industry_factor_transpose;
truncate table lkp_reporting_date;
truncate table portfolio_expected_sum;
truncate table portfolio_loss_aggr_es;
truncate table portfolio_loss_histogram_percentage;
truncate table portfolio_loss_histogram;
truncate table portfolio_loss_simulation_aggr;
truncate table portfolio_loss_simulation_el;
truncate table portfolio_loss_simulation;
truncate table portfolio_loss_summary;
truncate table portfolio_var_heatmap_buckets;


load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/customer_idiosyncratic_risk.csv' ignore into table customer_idiosyncratic_risk fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/customer_portfolio_1000.csv' ignore into table customer_portfolio fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\r';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_correlation_transpose.csv' ignore into table industry_factor_correlation_transpose fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_correlation.csv' ignore into table industry_factor_correlation fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_mean.csv' ignore into table industry_factor_mean fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/industry_factor_simulation.csv' ignore into table industry_factor_simulation fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/lkp_reporting_date.csv' ignore into table lkp_reporting_date fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';
load data local infile '/user/hive/warehouse/framework/app/ecocap/data/csv/noheader/portfolio_var_heatmap_buckets.csv' ignore into table portfolio_var_heatmap_buckets fields terminated by ',' enclosed by '"' escaped by '"' lines terminated by '\n';



