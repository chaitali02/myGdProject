DROP TABLE IF EXISTS CUSTOMER_ES_ALLOCATION; 

CREATE TABLE CUSTOMER_ES_ALLOCATION 
  ( 
     CUST_ID         STRING, 
     ES_CONTRIBUTION DECIMAL, 
     ES_ALLOCATION   DECIMAL, 
     REPORTING_DATE  STRING, 
     VERSION         INT 
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';