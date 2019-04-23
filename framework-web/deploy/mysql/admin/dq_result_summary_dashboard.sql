drop view if exists dq_result_summary_dashboard;
CREATE VIEW dq_result_summary_dashboard
AS
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'NULL' as check_type,null_pass_count as check_pass_count,null_fail_count as check_fail_count,null_score as score, 'COMPLETENESS' as dimension,version from dq_result_summary where null_pass_count > 0 OR null_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'VALUE' as check_type,value_pass_count as check_pass_count,value_fail_count as check_fail_count,value_score as score, 'CONSISTENCY' as dimension,version from dq_result_summary where value_pass_count > 0 OR value_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'RANGE' as check_type,range_pass_count as check_pass_count,range_fail_count as check_fail_count,range_score as score, 'CONFORMITY' as dimension,version from dq_result_summary where range_pass_count > 0 OR range_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'DATATYPE' as check_type,datatype_pass_count as check_pass_count,datatype_fail_count as check_fail_count,datatype_score as score, 'CONFORMITY' as dimension,version from dq_result_summary where datatype_pass_count > 0 OR datatype_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'FORMAT' as check_type,format_pass_count as check_pass_count,format_fail_count as check_fail_count,format_score as score, 'CONFORMITY' as dimension,version from dq_result_summary where format_pass_count > 0 OR format_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'LENGTH' as check_type,length_pass_count as check_pass_count,length_fail_count as check_fail_count,length_score as score, 'CONFORMITY' as dimension,version from dq_result_summary where length_pass_count > 0 OR length_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'REFINT' as check_type,refint_pass_count as check_pass_count,refint_fail_count as check_fail_count,refint_score as score, 'INTEGRITY' as dimension,version from dq_result_summary where refint_pass_count > 0 OR refint_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'DUPLICATE' as check_type,dup_pass_count as check_pass_count,dup_fail_count as check_fail_count,dup_score as score, 'INTEGRITY' as dimension,version from dq_result_summary where dup_pass_count > 0 OR dup_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'CUSTOM' as check_type,custom_pass_count as check_pass_count,custom_fail_count as check_fail_count,custom_score as score, 'CONFORMITY' as dimension,version from dq_result_summary where custom_pass_count > 0 OR custom_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'DOMAIN' as check_type,domain_pass_count as check_pass_count,domain_fail_count as check_fail_count,domain_score as score, 'ACCURACY' as dimension,version from dq_result_summary where domain_pass_count > 0 OR domain_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'BLANKSPACE' as check_type,blankspace_pass_count as check_pass_count,blankspace_fail_count as check_fail_count,blankspace_score as score, 'COMPLETENESS' as dimension,version from dq_result_summary where blankspace_pass_count > 0 OR blankspace_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'EXPRESSION' as check_type,expression_pass_count as check_pass_count,expression_fail_count as check_fail_count,expression_score as score, 'TIMELINESS' as dimension,version from dq_result_summary where expression_pass_count > 0 OR expression_fail_count > 0
union all
select rule_uuid,rule_version,rule_name,datapod_uuid,datapod_version,datapod_name,attribute_id,attribute_name,'CASE' as check_type,case_pass_count as check_pass_count,case_fail_count as check_fail_count,case_score as score, 'CONSISTENCY' as dimension,version from dq_result_summary where case_pass_count > 0 OR case_fail_count > 0
