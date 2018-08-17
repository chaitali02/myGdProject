invalidate metadata;
select 'customer_idiosyncratic_risk' as tabname,count(*) as cnt from ecocap.customer_idiosyncratic_risk
union
select 'customer_portfolio' as tabname,count(*) as cnt from ecocap.customer_portfolio
union
select 'industry_factor_correlation_transpose' as tabname,count(*) as cnt from ecocap.industry_factor_correlation_transpose
union
select 'industry_factor_correlation' as tabname,count(*) as cnt from ecocap.industry_factor_correlation
union
select 'industry_factor_mean' as tabname,count(*) as cnt from ecocap.industry_factor_mean
union
select 'industry_factor_simulation' as tabname,count(*) as cnt from ecocap.industry_factor_simulation
union
select 'lkp_reporting_date' as tabname,count(*) as cnt from ecocap.lkp_reporting_date
union
select 'portfolio_var_heatmap_buckets' as tabname,count(*) as cnt from ecocap.portfolio_var_heatmap_buckets;