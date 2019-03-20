DROP TABLE IF EXISTS dq_rule_summary; 

CREATE TABLE dq_rule_summary 
  ( 
     rule_exec_uuid    VARCHAR(50), 
     rule_exec_version INT(10), 
     rule_exec_time   VARCHAR(100), 
     rule_uuid        VARCHAR(50), 
     rule_version     INT(10), 
     rule_name        VARCHAR(100), 
     datapod_uuid     VARCHAR(50), 
     datapod_version  INT(10), 
     datapod_name     VARCHAR(100), 
     total_row_count  INT(10), 
     total_pass_count INT(10), 
     total_fail_count INT(10), 
     threshold_type   VARCHAR(50), 
     threshold_limit  INT(3), 
     threshold_ind    VARCHAR(6), 
     score            INT(3), 
     version          INT(10) 
  ); 