DROP TABLE IF EXISTS rule_result_summary; 

CREATE TABLE rule_result_summary 
  ( 
     rule_uuid    VARCHAR(50), 
     rule_version INT(10), 
     rule_name    VARCHAR(100), 
     entity_type  VARCHAR(50), 
     entity_id    VARCHAR(50), 
     score        DECIMAL(6, 3), 
     version      INT(10) 
  ); 