DROP TABLE IF EXISTS INDUSTRY_FACTOR_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_TRANSPOSE
             ( 
                          ITERATION_ID   INTEGER, 
                          REPORTING_DATE VARCHAR(50), 
                          FACTOR         VARCHAR(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
