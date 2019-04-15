DROP TABLE dp_result_summary; 

CREATE TABLE dp_result_summary 
  ( 
     rule_exec_uuid    VARCHAR2(50), 
     rule_exec_version INTEGER, 
     rule_exec_time    VARCHAR2(100), 
     rule_uuid         VARCHAR2(50), 
     rule_version      INTEGER, 
     rule_name         VARCHAR2(100), 
     datapod_uuid      VARCHAR2(50), 
     datapod_version   INTEGER, 
     datapod_name      VARCHAR2(100), 
     attribute_id      VARCHAR2(50), 
     attribute_name    VARCHAR2(100), 
     num_rows          INTEGER, 
     min_val           VARCHAR2(50), 
     max_val           VARCHAR2(50), 
     avg_val           VARCHAR2(50), 
     median_val        VARCHAR2(50), 
     std_dev           VARCHAR2(50), 
     num_distinct      INTEGER, 
     perc_distinct     VARCHAR2(50), 
     num_null          INTEGER, 
     perc_null         VARCHAR2(50), 
     min_length        VARCHAR2(50), 
     max_length        VARCHAR2(50), 
     avg_length        VARCHAR2(50), 
     num_duplicates    INTEGER, 
     version           INTEGER
  ); 

CREATE INDEX version_index ON dp_result_summary(version);
