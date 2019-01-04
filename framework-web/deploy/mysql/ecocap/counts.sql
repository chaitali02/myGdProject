select tabname,cnt from
(
select 'customer_es_allocation' as tabname, count(*) as cnt from customer_es_allocation
union
select 'customer_generate_data' as tabname, count(*) as cnt from customer_generate_data
union
select 'customer_idiosyncratic_risk' as tabname, count(*) as cnt from customer_idiosyncratic_risk
union
select 'customer_idiosyncratic_transpose_stage' as tabname, count(*) as cnt from customer_idiosyncratic_transpose_stage
union
select 'customer_idiosyncratic_transpose' as tabname, count(*) as cnt from customer_idiosyncratic_transpose
union
select 'customer_loss_simulation' as tabname, count(*) as cnt from customer_loss_simulation
union
select 'customer_portfolio_clone' as tabname, count(*) as cnt from customer_portfolio_clone
union
select 'customer_portfolio_ul_calc_allocation' as tabname, count(*) as cnt from customer_portfolio_ul_calc_allocation
union
select 'customer_portfolio_ul_calc_summary' as tabname, count(*) as cnt from customer_portfolio_ul_calc_summary
union
select 'customer_portfolio_ul_calc' as tabname, count(*) as cnt from customer_portfolio_ul_calc
union
select 'customer_portfolio_ul' as tabname, count(*) as cnt from customer_portfolio_ul
union
select 'customer_portfolio' as tabname, count(*) as cnt from customer_portfolio
union
select 'customer_var_contribution_topn_perc' as tabname, count(*) as cnt from customer_var_contribution_topn_perc
union
select 'industry_factor_correlation_transpose' as tabname, count(*) as cnt from industry_factor_correlation_transpose
union
select 'industry_factor_correlation' as tabname, count(*) as cnt from industry_factor_correlation
union
select 'industry_factor_mean' as tabname, count(*) as cnt from industry_factor_mean
union
select 'industry_factor_simulation_stage' as tabname, count(*) as cnt from industry_factor_simulation_stage
union
select 'industry_factor_simulation' as tabname, count(*) as cnt from industry_factor_simulation
union
select 'industry_factor_transpose' as tabname, count(*) as cnt from industry_factor_transpose
union
select 'lkp_reporting_date' as tabname, count(*) as cnt from lkp_reporting_date
union
select 'portfolio_expected_sum' as tabname, count(*) as cnt from portfolio_expected_sum
union
select 'portfolio_loss_aggr_es' as tabname, count(*) as cnt from portfolio_loss_aggr_es
union
select 'portfolio_loss_histogram_percentage' as tabname, count(*) as cnt from portfolio_loss_histogram_percentage
union
select 'portfolio_loss_histogram' as tabname, count(*) as cnt from portfolio_loss_histogram
union
select 'portfolio_loss_simulation_aggr' as tabname, count(*) as cnt from portfolio_loss_simulation_aggr
union
select 'portfolio_loss_simulation_el' as tabname, count(*) as cnt from portfolio_loss_simulation_el
union
select 'portfolio_loss_simulation' as tabname, count(*) as cnt from portfolio_loss_simulation
union
select 'portfolio_var_heatmap_buckets' as tabname, count(*) as cnt from portfolio_var_heatmap_buckets
) t1
order by tabname;