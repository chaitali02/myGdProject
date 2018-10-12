
DROP TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE
             ( 
                          ITERATIONID    INTEGER, 
                          REPORTING_DATE VARCHAR2(50), 
                          CUSTOMER       VARCHAR2(50), 
                          PD             DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
