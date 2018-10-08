DROP TABLE IF EXISTS ACCOUNT_TYPE; 

CREATE TABLE ACCOUNT_TYPE 
  ( 
     ACCOUNT_TYPE_ID   STRING, 
     ACCOUNT_TYPE_CODE STRING, 
     ACCOUNT_TYPE_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';