select tabname,cnt from
(
select 'cc_transactions' as tabname, count(*) as cnt from ccfruad_small.cc_transactions
union
select 'cc_transactions_predictions' as tabname, count(*) as cnt from ccfruad_small.cc_transactions_predictions
union
select 'dim_address' as tabname, count(*) as cnt from dim_address
union
select 'dim_bank' as tabname, count(*) as cnt from dim_bank
union
select 'dim_branch' as tabname, count(*) as cnt from dim_branch
union
select 'dim_country' as tabname, count(*) as cnt from dim_country
union
select 'dim_customer' as tabname, count(*) as cnt from dim_customer
union
select 'dim_transaction_type' as tabname, count(*) as cnt from dim_transaction_type
union
select 'dp_rule_results' as tabname, count(*) as cnt from dp_rule_results
union
select 'dq_rule_results' as tabname, count(*) as cnt from dq_rule_results
union
select 'fact_account_summary_monthly' as tabname, count(*) as cnt from fact_account_summary_monthly
union
select 'fact_customer_summary_monthly' as tabname, count(*) as cnt from fact_customer_summary_monthly
union
select 'fact_transaction' as tabname, count(*) as cnt from fact_transaction
union
select 'rc_rule_results' as tabname, count(*) as cnt from rc_rule_results
) t1
order by tabname;
