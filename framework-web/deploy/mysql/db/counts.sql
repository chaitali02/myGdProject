select tabname,cnt from
(
select 'dp_result_summary' as tabname, count(*) as cnt from framework_db.dp_result_summary
union
select 'dq_result_summary' as tabname, count(*) as cnt from framework_db.dq_result_summary
union
select 'dq_result_detail' as tabname, count(*) as cnt from framework_db.dq_result_detail
union
select 'equity_orders' as tabname, count(*) as cnt from framework_db.equity_orders
union
select 'equity_executions' as tabname, count(*) as cnt from framework_db.equity_executions
union
select 'security' as tabname, count(*) as cnt from framework_db.security
union
select 'rule_result_detail' as tabname, count(*) as cnt from framework_db.rule_result_detail
union
select 'rule_result_summary' as tabname, count(*) as cnt from framework_db.rule_result_summary
union
select 'account_summary_daily' as tabname, count(*) as cnt from framework_db.account_summary_daily

) t1
order by tabname;
