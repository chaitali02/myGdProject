select tabname,cnt from
(
select 'fact_transaction_journal' as tabname, count(*) as cnt from aml_small.fact_transaction_journal
union
select 'customer_alert_summary' as tabname, count(*) as cnt from aml_small.customer_alert_summary
union
select 'rule_alert_summary' as tabname, count(*) as cnt from aml_small.rule_alert_summary
union
select 'country_alert_summary' as tabname, count(*) as cnt from aml_small.country_alert_summary
union
select 'predict_suspicious_activity_gbt' as tabname, count(*) as cnt from aml_small.predict_suspicious_activity_gbt
union
select 'predict_suspicious_activity_lr' as tabname, count(*) as cnt from aml_small.predict_suspicious_activity_lr
) t1
order by tabname;
