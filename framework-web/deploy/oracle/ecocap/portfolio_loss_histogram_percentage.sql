DROP TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE
             ( 
                          REPORTING_DATE VARCHAR2(50), 
                          BUCKET         VARCHAR2(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );
