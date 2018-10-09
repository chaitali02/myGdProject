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




LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/customer_portfolio_1000.csv' IGNORE INTO TABLE customer_portfolio FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_factor_correlation_transpose.csv' IGNORE INTO TABLE industry_factor_correlation_transpose FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_factor_correlation.csv' IGNORE INTO TABLE industry_factor_correlation FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/industry_factor_mean.csv' IGNORE INTO TABLE industry_factor_mean FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/lkp_reporting_date.csv' IGNORE INTO TABLE lkp_reporting_date FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/portfolio_var_heatmap_buckets.csv' IGNORE INTO TABLE portfolio_var_heatmap_buckets FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;








