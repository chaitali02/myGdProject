select tabname,cnt from
(
select 'security' as tabname, count(*) as cnt from security
union
select 'equity_orders' as tabname, count(*) as cnt from equity_orders
union
select 'equity_executions' as tabname, count(*) as cnt from equity_executions
union
select 'rule_result_summary' as tabname, count(*) as cnt from rule_result_summary
union
select 'dq_result_summary' as tabname, count(*) as cnt from dq_result_summary
union


select 'account_mysql' as tabname, count(*) as cnt from account_mysql
union
select 'account' as tabname, count(*) as cnt from account
union
select 'account_status_type' as tabname, count(*) as cnt from account_status_type
union
select 'account_type' as tabname, count(*) as cnt from account_type
union
select 'address' as tabname, count(*) as cnt from address
union
select 'bank' as tabname, count(*) as cnt from bank
union
select 'branch' as tabname, count(*) as cnt from branch
union
select 'branch_type' as tabname, count(*) as cnt from branch_type
union
select 'customer' as tabname, count(*) as cnt from customer
union
select 'dim_date' as tabname, count(*) as cnt from dim_date
union
select 'dim_state' as tabname, count(*) as cnt from dim_state
union
select 'product_type' as tabname, count(*) as cnt from product_type
union
select 'transaction' as tabname, count(*) as cnt from transaction
union
select 'transaction_type' as tabname, count(*) as cnt from transaction_type
union
select 'dim_account' as tabname, count(*) as cnt from dim_account
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


select 'fact_account_summary_monthly' as tabname, count(*) as cnt from fact_account_summary_monthly
union
select 'fact_customer_summary_monthly' as tabname, count(*) as cnt from fact_customer_summary_monthly
union
select 'fact_transaction' as tabname, count(*) as cnt from fact_transaction

) t1
order by tabname; 
