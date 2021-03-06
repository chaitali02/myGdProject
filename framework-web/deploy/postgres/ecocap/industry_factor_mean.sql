DROP TABLE IF EXISTS INDUSTRY_FACTOR_MEAN;

CREATE TABLE INDUSTRY_FACTOR_MEAN 
             ( 
                          ID             VARCHAR(50), 
                          MEAN           DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
