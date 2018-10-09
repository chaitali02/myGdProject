DROP TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE
             ( 
                          FACTOR_X       VARCHAR(50), 
                          REPORTING_DATE VARCHAR(50), 
                          FACTOR_Y       VARCHAR(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
