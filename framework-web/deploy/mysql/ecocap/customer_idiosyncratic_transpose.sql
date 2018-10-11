DROP TABLE IF EXISTS CUSTOMER_IDIOSYNCRATIC_TRANSPOSE; 

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE 
  ( 
     ITERATIONID    INTEGER(50), 
     REPORTING_DATE VARCHAR(50), 
     CUSTOMER       VARCHAR(50), 
     PD             DECIMAL(10, 2), 
     VERSION        INTEGER(50) 
  ); 