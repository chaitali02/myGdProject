DROP TABLE IF EXISTS DIM_BRANCH; 

CREATE TABLE DIM_BRANCH 
  ( 
     BRANCH_ID            STRING, 
     SRC_BRANCH_ID        STRING, 
     BRANCH_TYPE_CODE     STRING, 
     BRANCH_NAME          STRING, 
     BRANCH_DESC          STRING, 
     BRANCH_CONTACT_NAME  STRING, 
     BRANCH_CONTACT_PHONE STRING, 
     BRANCH_CONTACT_EMAIL STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';