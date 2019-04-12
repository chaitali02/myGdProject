DROP TABLE rule_result_detail; 

CREATE TABLE rule_result_detail 
  ( 
     rule_exec_uuid    VARCHAR2(50), 
     rule_exec_version INTEGER, 
     rule_exec_time    VARCHAR(100), 
     rule_uuid         VARCHAR2(50), 
     rule_version      INTEGER, 
     rule_name         VARCHAR2(100), 
     entity_type       VARCHAR2(50), 
     entity_id         VARCHAR2(50), 
     criteria_id       VARCHAR2(50), 
     criteria_name     VARCHAR(100), 
     criteria_met_ind  VARCHAR2(50), 
     criteria_expr     VARCHAR2(500), 
     criteria_score    DECIMAL(6, 3), 
     version           INTEGER
  ); 
CREATE INDEX rule_result_detail_version_index ON rule_result_detail(version);
