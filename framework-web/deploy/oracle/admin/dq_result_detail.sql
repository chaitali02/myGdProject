DROP TABLE dq_result_detail; 

CREATE TABLE dq_result_detail 
  ( 
     rule_exec_uuid        VARCHAR2(50), 
     rule_exec_version     INTEGER, 
     rule_exec_time        VARCHAR2(100), 
     rule_uuid             VARCHAR2(50), 
     rule_version          INTEGER, 
     rule_name             VARCHAR2(100), 
     rule_desc             VARCHAR2(500),
     datapod_uuid          VARCHAR2(50), 
     datapod_version       INTEGER, 
     datapod_name          VARCHAR2(100),
     datapod_desc          VARCHAR2(500),  
     attribute_id          VARCHAR2(50), 
     attribute_name        VARCHAR2(100), 
     attribute_value       VARCHAR2(500),
     attribute_desc        VARCHAR2(500),   
     pii_flag	           VARCHAR2(1),
     cde_flag	           VARCHAR2(1),
     rowkey_name           VARCHAR2(500), 
     rowkey_value          VARCHAR2(500), 
     null_check_pass       VARCHAR2(1), 
     value_check_pass      VARCHAR2(1), 
     range_check_pass      VARCHAR2(1), 
     datatype_check_pass   VARCHAR2(1), 
     format_check_pass     VARCHAR2(1), 
     length_check_pass     VARCHAR2(1), 
     ri_check_pass         VARCHAR2(1), 
     dup_check_pass        VARCHAR2(1), 
     custom_check_pass     VARCHAR2(1), 
     domain_check_pass     VARCHAR2(1), 
     blankspace_check_pass VARCHAR2(1), 
     expression_check_pass VARCHAR2(1),  
     case_check_pass 	   VARCHAR2(1), 
     all_check_pass        VARCHAR2(1), 
     param_info            VARCHAR2(500),
     version               INTEGER
  ); 

CREATE INDEX dq_result_detail_version_index ON dq_result_detail(version);
