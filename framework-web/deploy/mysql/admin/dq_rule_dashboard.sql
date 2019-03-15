drop view if exists dq_rule_dashboard;
CREATE VIEW dq_rule_dashboard
AS
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'NULL' as check_type,CASE WHEN null_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'COMPLETENESS' as dimension,version from dq_rule_detail where null_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'VALUE' as check_type,CASE WHEN value_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CONSISTENCY' as dimension,version from dq_rule_detail where value_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'RANGE' as check_type,CASE WHEN range_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CONFORMITY' as dimension,version from dq_rule_detail where range_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'DATATYPE' as check_type,CASE WHEN datatype_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CONFORMITY' as dimension,version from dq_rule_detail where datatype_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'FORMAT' as check_type,CASE WHEN format_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CONFORMITY' as dimension,version from dq_rule_detail where format_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'LENGTH' as check_type,CASE WHEN length_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CONFORMITY' as dimension,version from dq_rule_detail where length_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'REFINT' as check_type,CASE WHEN ri_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'INTEGRITY' as dimension,version from dq_rule_detail where ri_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'DUPLICATE' as check_type,CASE WHEN dup_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'INTEGRITY' as dimension,version from dq_rule_detail where dup_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'CUSTOM' as check_type,CASE WHEN custom_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CUSTOM' as dimension,version from dq_rule_detail where custom_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'DOMAIN' as check_type,CASE WHEN domain_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'ACCURACY' as dimension,version from dq_rule_detail where domain_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'BLANKSPACE' as check_type,CASE WHEN blankspace_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'COMPLETENESS' as dimension,version from dq_rule_detail where blankspace_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'EXPRESSION' as check_type,CASE WHEN expression_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'TIMELINESS' as dimension,version from dq_rule_detail where expression_check_pass IN ('Y','N')
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,attribute_value,rowkey_name,
rowkey_value, 'CASE' as check_type,CASE WHEN case_check_pass = 'Y' THEN 'PASS' ELSE 'FAIL' END as check_pass,'CONSISTENCY' as dimension,version from dq_rule_detail where case_check_pass IN ('Y','N');