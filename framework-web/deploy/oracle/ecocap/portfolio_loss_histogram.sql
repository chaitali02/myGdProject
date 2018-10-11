DROP TABLE PORTFOLIO_LOSS_HISTOGRAM;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          BUCKET         VARCHAR(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );