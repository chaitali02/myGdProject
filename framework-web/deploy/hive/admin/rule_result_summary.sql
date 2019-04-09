DROP TABLE IF EXISTS rule_result_summary; 

CREATE TABLE rule_result_summary 
  (  
     rule_exec_uuid    STRING, 
     rule_exec_version INT, 
     rule_exec_time    STRING, 
     rule_uuid         STRING, 
     rule_version      INT, 
     rule_name         STRING, 
     entity_type       STRING, 
     entity_id         STRING, 
     score             DECIMAL,
     filter_expr       STRING,      
     version           INT
  ); 
