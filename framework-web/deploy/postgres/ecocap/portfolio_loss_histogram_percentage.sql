DROP TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          BUCKET         VARCHAR(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );