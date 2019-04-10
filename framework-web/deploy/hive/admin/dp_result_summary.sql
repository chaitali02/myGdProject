DROP TABLE IF EXISTS dp_result_summary; 

CREATE TABLE dp_result_summary 
  ( 
     rule_exec_uuid    STRING, 
     rule_exec_version INT, 
     rule_exec_time    STRING, 
     rule_uuid         STRING, 
     rule_version      INT, 
     rule_name         STRING, 
     datapod_uuid      STRING, 
     datapod_version   INT, 
     datapod_name      STRING, 
     attribute_id      STRING, 
     attribute_name    STRING, 
     num_rows          INT, 
     min_val           STRING, 
     max_val           STRING, 
     avg_val           STRING, 
     median_val        STRING, 
     std_dev           STRING, 
     num_distinct      INT, 
     perc_distinct     STRING, 
     num_null          INT, 
     perc_null         STRING, 
     min_length        STRING, 
     max_length        STRING, 
     avg_length        STRING, 
     num_duplicates    INT, 
     version           INT
  ); 
