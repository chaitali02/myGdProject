DROP TABLE IF EXISTS rule_result_detail; 

CREATE TABLE rule_result_detail 
  ( 
     rule_exec_uuid    STRING, 
     rule_exec_version INT, 
     rule_exec_time    STRING, 
     rule_uuid         STRING, 
     rule_version      INT, 
     rule_name         STRING, 
     entity_type       STRING, 
     entity_id         STRING, 
     criteria_id       STRING, 
     criteria_name     STRING, 
     criteria_met_ind  STRING, 
     criteria_expr     STRING, 
     criteria_score    DECIMAL, 
     version           INT
  ); 
