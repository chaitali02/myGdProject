select tabname,cnt from
(
select 'account_hive' as tabname, count(*) as cnt from account_hive
union all
select 'account' as tabname, count(*) as cnt from account
union all
select 'account_status_type' as tabname, count(*) as cnt from account_status_type
union all
select 'account_type' as tabname, count(*) as cnt from account_type
union all
select 'address' as tabname, count(*) as cnt from address
union all
select 'bank' as tabname, count(*) as cnt from bank
union all
select 'branch' as tabname, count(*) as cnt from branch
union all
select 'branch_type' as tabname, count(*) as cnt from branch_type
union all
select 'dim_country' as tabname, count(*) as cnt from dim_country
union all
select 'customer' as tabname, count(*) as cnt from customer
union all
select 'dim_date' as tabname, count(*) as cnt from dim_date
union all
select 'product_type' as tabname, count(*) as cnt from product_type
union all
select 'transaction' as tabname, count(*) as cnt from transaction
union all
select 'transaction_type' as tabname, count(*) as cnt from transaction_type
union all
select 'dim_account' as tabname, count(*) as cnt from dim_account
union all
select 'dim_address' as tabname, count(*) as cnt from dim_address
union all
select 'dim_bank' as tabname, count(*) as cnt from dim_bank
union all
select 'dim_branch' as tabname, count(*) as cnt from dim_branch
union all
select 'dim_country' as tabname, count(*) as cnt from dim_country
union all
select 'dim_customer' as tabname, count(*) as cnt from dim_customer
union all
select 'dim_transaction_type' as tabname, count(*) as cnt from dim_transaction_type
union all
select 'dp_rule_results' as tabname, count(*) as cnt from dp_rule_results
union all
select 'dq_rule_results' as tabname, count(*) as cnt from dq_rule_results
union all
select 'fact_account_summary_monthly' as tabname, count(*) as cnt from fact_account_summary_monthly
union all
select 'fact_customer_summary_monthly' as tabname, count(*) as cnt from fact_customer_summary_monthly
union all
select 'fact_transaction' as tabname, count(*) as cnt from fact_transaction
union all
select 'rc_rule_results' as tabname, count(*) as cnt from rc_rule_results
) t1
order by tabname; 
