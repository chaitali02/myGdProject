DROP TABLE IF EXISTS dq_result_summary; 

CREATE TABLE dq_result_summary 
  ( 
     rule_exec_uuid        VARCHAR(50), 
     rule_exec_version     INT(10), 
     rule_exec_time        VARCHAR(50), 
     rule_uuid             VARCHAR(50), 
     rule_version          VARCHAR(50), 
     rule_name             VARCHAR(50), 
     datapod_uuid          VARCHAR(50), 
     datapod_version       VARCHAR(50), 
     datapod_name          VARCHAR(50), 
     null_pass_count       INT(10), 
     null_fail_count       INT(10), 
     null_score            INT(10), 
     value_pass_count      INT(10), 
     value_fail_count      INT(10), 
     value_score           INT(10), 
     range_pass_count      INT(10), 
     range_fail_count      INT(10), 
     range_score           INT(10), 
     datatype_pass_count   INT(10), 
     datatype_fail_count   INT(10), 
     datatype_score        INT(10), 
     format_pass_count     INT(10), 
     format_fail_count     INT(10), 
     format_score          INT(10), 
     length_pass_count     INT(10), 
     length_fail_count     INT(10), 
     length_score          INT(10), 
     refint_pass_count     INT(10), 
     refint_fail_count     INT(10), 
     refintegrity_score    INT(10), 
     dup_pass_count        INT(10), 
     dup_fail_count        INT(10), 
     dup_score             INT(10), 
     custom_pass_count     INT(10), 
     custom_fail_count     INT(10), 
     custom_score          INT(10), 
     domain_pass_count     INT(10), 
     domain_fail_count     INT(10), 
     domain_score          INT(10), 
     blank_pass_count      INT(10), 
     blank_fail_count      INT(10), 
     blank_score           INT(10), 
     expression_pass_count INT(10), 
     expression_fail_count INT(10), 
     expression_score      INT(10), 
     case_pass_count       INT(10), 
     case_fail_count       INT(10), 
     case_score            INT(10), 
     total_row_count       INT(10), 
     total_pass_count      INT(10), 
     total_fail_count      INT(10), 
     threshold_type        VARCHAR(50), 
     threshold_limit       INT(10), 
     threshold_ind         VARCHAR(50), 
     score                 INT(10), 
     version               INT(10), 
     INDEX (version), 
     INDEX (rule_uuid, datapod_uuid) 
  ); 
