DROP TABLE IF EXISTS INDUSTRY_FACTOR_CORRELATION;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION
             ( 
                          FACTOR         VARCHAR(50), 
                          FACTOR1        DECIMAL(10,2), 
                          FACTOR2        DECIMAL(10,2), 
                          FACTOR3        DECIMAL(10,2), 
                          FACTOR4        DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
