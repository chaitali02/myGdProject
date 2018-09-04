invalidate metadata;

select 'account' as input_table, count(*) as cnt from account
union
select 'account_status_type' as input_table, count(*) as cnt from account_status_type
union
select 'account_type' as input_table, count(*) as cnt from account_type
union
select 'address' as input_table, count(*) as cnt from address
union
select 'bank' as input_table, count(*) as cnt from bank
union
select 'branch' as input_table, count(*) as cnt from branch
union
select 'branch_type' as input_table, count(*) as cnt from branch_type
union
select 'customer' as input_table, count(*) as cnt from customer
union
select 'dim_date' as input_table, count(*) as cnt from dim_date
union
select 'product_type' as input_table, count(*) as cnt from product_type
union
select 'transaction' as input_table, count(*) as cnt from transaction
union
select 'transaction_type' as input_table, count(*) as cnt from transaction_type
union
select 'dim_account' as output_table, count(*) as cnt from dim_account
union
select 'dim_address' as output_table, count(*) as cnt from dim_address
union
select 'dim_bank' as output_table, count(*) as cnt from dim_bank
union
select 'dim_branch' as output_table, count(*) as cnt from dim_branch
union
select 'dim_customer' as output_table, count(*) as cnt from dim_customer
union
select 'dim_transaction_type' as output_table, count(*) as cnt from dim_transaction_type
union
select 'dp_rule_results' as output_table, count(*) as cnt from dp_rule_results
union
select 'dq_rule_results' as output_table, count(*) as cnt from dq_rule_results
union
select 'fact_account_summary_monthly' as output_table, count(*) as cnt from fact_account_summary_monthly
union
select 'fact_customer_summary_monthly' as output_table, count(*) as cnt from fact_customer_summary_monthly
union
select 'fact_transaction' as output_table, count(*) as cnt from fact_transaction
union
select 'profile_rule_results' as output_table, count(*) as cnt from profile_rule_results
union
select 'rc_rule_results' as input_output, count(*) as cnt from rc_rule_results;