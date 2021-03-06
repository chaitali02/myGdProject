DROP TABLE IF EXISTS BRANCH_TYPE; 

CREATE TABLE BRANCH_TYPE 
  ( 
     BRANCH_TYPE_ID   STRING, 
     BRANCH_TYPE_CODE STRING, 
     BRANCH_TYPE_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; 