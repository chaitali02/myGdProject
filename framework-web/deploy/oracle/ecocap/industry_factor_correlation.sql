DROP TABLE INDUSTRY_FACTOR_CORRELATION;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION
             ( 
                          FACTOR         VARCHAR2(50), 
                          FACTOR1        DECIMAL(10,2), 
                          FACTOR2        DECIMAL(10,2), 
                          FACTOR3        DECIMAL(10,2), 
                          FACTOR4        DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR2(50), 
                          VERSION        INTEGER 
             );
