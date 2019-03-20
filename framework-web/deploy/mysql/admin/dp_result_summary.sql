DROP TABLE IF EXISTS dp_result_summary; 

CREATE TABLE dp_result_summary 
  ( 
     rule_exec_uuid    VARCHAR(50), 
     rule_exec_version INT(10), 
     rule_exec_time    VARCHAR(100), 
     rule_uuid         VARCHAR(50), 
     rule_version      INT(10), 
     rule_name         VARCHAR(100), 
     datapod_uuid      VARCHAR(50), 
     datapod_version   INT(10), 
     datapod_name      VARCHAR(100), 
     attribute_id      VARCHAR(50), 
     attribute_name    VARCHAR(100), 
     num_rows          INT(10), 
     min_val           VARCHAR(50), 
     max_val           VARCHAR(50), 
     avg_val           VARCHAR(50), 
     median_val        VARCHAR(50), 
     std_dev           VARCHAR(50), 
     num_distinct      INT(10), 
     perc_distinct     VARCHAR(50), 
     num_null          INTEGER(10), 
     perc_null         VARCHAR(50), 
     min_length        VARCHAR(50), 
     max_length        VARCHAR(50), 
     avg_length        VARCHAR(50), 
     num_duplicates    INT(10), 
     version           INT(10) 
  ); 