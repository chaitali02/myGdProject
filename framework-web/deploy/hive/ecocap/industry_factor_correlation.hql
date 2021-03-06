DROP TABLE IF EXISTS INDUSTRY_FACTOR_CORRELATION; 

CREATE TABLE INDUSTRY_FACTOR_CORRELATION 
  ( 
     FACTOR         STRING, 
     FACTOR1        DECIMAL, 
     FACTOR2        DECIMAL, 
     FACTOR3        DECIMAL, 
     FACTOR4        DECIMAL, 
     REPORTING_DATE STRING, 
     VERSION        INT 
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','; 