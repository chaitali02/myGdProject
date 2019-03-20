DROP TABLE IF EXISTS dp_result_summary; 

CREATE TABLE dp_result_summary 
  ( 
     rule_exec_uuid    VARCHAR(50), 
     rule_exec_version INT(10), 
     rule_exec_time    VARCHAR(100), 
     rule_uuid         VARCHAR(50), 
     rule_version      INT(10), 
     rule_name         VARCHAR(100), 
     datapoduuid       VARCHAR(50) , 
     datapodversion    VARCHAR(50), 
     datapodname       VARCHAR(100), 
     attributeid       VARCHAR(50), 
     attributename     VARCHAR(100), 
     numrows           VARCHAR(50), 
     minval            DECIMAL(20, 4), 
     maxval            DECIMAL(20, 4), 
     avgval            DECIMAL(20, 4), 
     medianval         DECIMAL(20, 4), 
     stddev            DECIMAL(20, 4), 
     numdistinct       INTEGER(10), 
     perdistinct       DECIMAL(20, 4), 
     numnull           INTEGER(10), 
     pernull           DECIMAL(20, 4), 
     minlength         DECIMAL(20, 4), 
     maxlength         DECIMAL(20, 4), 
     avglength         DECIMAL(20, 4), 
     numduplicates     DECIMAL(20, 4), 
     version           INTEGER(10) 
  ); 
