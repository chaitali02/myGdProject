DROP TABLE PORTFOLIO_LOSS_HISTOGRAM;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM
             ( 
                          REPORTING_DATE VARCHAR2(50), 
                          BUCKET         VARCHAR2(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );
