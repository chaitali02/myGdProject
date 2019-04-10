DROP TABLE IF EXISTS dq_result_summary; 

CREATE TABLE dq_result_summary 
  ( 
     rule_exec_uuid        STRING, 
     rule_exec_version     INT, 
     rule_exec_time        STRING, 
     rule_uuid             STRING, 
     rule_version          INT, 
     rule_name             STRING, 
     rule_desc             STRING,
     datapod_uuid          STRING, 
     datapod_version       INT, 
     datapod_name          STRING,
     datapod_desc          STRING,  
     attribute_id          STRING, 
     attribute_name        STRING,
     attribute_desc        STRING,
     pii_flag              STRING,
     cde_flag              STRING,
     null_pass_count       INT, 
     null_fail_count       INT, 
     null_score            DECIMAL, 
     value_pass_count      INT, 
     value_fail_count      INT, 
     value_score           DECIMAL,
     range_pass_count      INT, 
     range_fail_count      INT, 
     range_score           DECIMAL,
     datatype_pass_count   INT, 
     datatype_fail_count   INT, 
     datatype_score        DECIMAL,
     format_pass_count     INT, 
     format_fail_count     INT, 
     format_score          DECIMAL,
     length_pass_count     INT, 
     length_fail_count     INT, 
     length_score          DECIMAL,
     refint_pass_count     INT, 
     refint_fail_count     INT, 
     refint_score          DECIMAL,
     dup_pass_count        INT, 
     dup_fail_count        INT, 
     dup_score             DECIMAL,
     custom_pass_count     INT, 
     custom_fail_count     INT, 
     custom_score          DECIMAL,
     domain_pass_count     INT, 
     domain_fail_count     INT, 
     domain_score          DECIMAL,
     blankspace_pass_count INT, 
     blankspace_fail_count INT, 
     blankspace_score      DECIMAL,
     expression_pass_count INT, 
     expression_fail_count INT, 
     expression_score      DECIMAL,
     case_pass_count       INT, 
     case_fail_count       INT, 
     case_score            DECIMAL,
     total_row_count       INT, 
     total_pass_count      INT, 
     total_fail_count      INT, 
     total_fail_percentage DECIMAL,
     threshold_type        STRING, 
     threshold_limit       INT, 
     threshold_ind         STRING, 
     score                 DECIMAL,
     param_info            STRING,
     version               INT
  ); 
