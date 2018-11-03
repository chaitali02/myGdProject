DROP TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE
             ( 
                          FACTOR_X       VARCHAR2(50), 
                          REPORTING_DATE VARCHAR2(50), 
                          FACTOR_Y       VARCHAR2(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
