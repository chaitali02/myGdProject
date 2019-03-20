DROP TABLE IF EXISTS dq_result_detail; 

CREATE TABLE dq_result_detail 
  ( 
     rule_exec_uuid        VARCHAR(50), 
     rule_exec_version     INT(10), 
     rule_exec_time        VARCHAR(100), 
     rule_uuid             VARCHAR(50), 
     rule_version          INT(10), 
     rule_name             VARCHAR(100), 
     datapod_uuid          VARCHAR(50), 
     datapod_version       INT(10), 
     datapod_name          VARCHAR(100), 
     attribute_id          VARCHAR(50), 
     attribute_name        VARCHAR(100), 
     attribute_value       VARCHAR(500), 
     rowkey_name           VARCHAR(500), 
     rowkey_value          VARCHAR(500), 
     null_check_pass       VARCHAR(1), 
     value_check_pass      VARCHAR(1), 
     range_check_pass      VARCHAR(1), 
     datatype_check_pass   VARCHAR(1), 
     format_check_pass     VARCHAR(1), 
     length_check_pass     VARCHAR(1), 
     ri_check_pass         VARCHAR(1), 
     dup_check_pass        VARCHAR(1), 
     custom_check_pass     VARCHAR(1), 
     domain_check_pass     VARCHAR(1), 
     blankspace_check_pass VARCHAR(1), 
     expression_check_pass VARCHAR(1),  
     case_check_pass 	   VARCHAR(1), 
     all_check_pass        VARCHAR(1), 
     version               INT(10) 
  ); 
