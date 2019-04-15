DROP TABLE rule_result_summary; 

CREATE TABLE rule_result_summary 
  (  
     rule_exec_uuid    VARCHAR2(50), 
     rule_exec_version INTEGER, 
     rule_exec_time    VARCHAR(100), 
     rule_uuid         VARCHAR2(50), 
     rule_version      INTEGER, 
     rule_name         VARCHAR(100), 
     entity_type       VARCHAR2(50), 
     entity_id         VARCHAR2(50), 
     score             DECIMAL(6, 3),
     filter_expr       VARCHAR(500),      
     version           INTEGER
  ); 
CREATE INDEX rule_result_summary_version_index ON rule_result_summary(version);
