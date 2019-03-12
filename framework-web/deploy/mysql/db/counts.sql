select tabname,cnt from
(
select 'dq_rule_summary' as tabname, count(*) as cnt from framework.dq_rule_summary
union
select 'dq_rule_detail' as tabname, count(*) as cnt from framework.dq_rule_detail
union
select 'equity_orders' as tabname, count(*) as cnt from framework.equity_orders
union
select 'equity_executions' as tabname, count(*) as cnt from framework.equity_executions
union
select 'security' as tabname, count(*) as cnt from framework.security
union
select 'rule_result_detail' as tabname, count(*) as cnt from framework.rule_result_detail
union
select 'rule_result_summary' as tabname, count(*) as cnt from framework.rule_result_summary
union
select 'account_summary_daily' as tabname, count(*) as cnt from framework.account_summary_daily

) t1
order by tabname;
