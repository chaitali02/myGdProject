DROP TABLE IF EXISTS rule_result_detail; 

CREATE TABLE rule_result_detail 
  ( 
     rule_uuid        VARCHAR(50), 
     rule_version     INT(10), 
     rule_name        VARCHAR(100), 
     entity_type      VARCHAR(50), 
     entity_id        VARCHAR(50), 
     criteria_id      VARCHAR(50), 
     criteria_name    VARCHAR(100), 
     criteria_met_ind VARCHAR(50), 
     criteria_expr    VARCHAR(500), 
     criteria_score   DECIMAL(6, 3), 
     version          INT(10) 
  ); 