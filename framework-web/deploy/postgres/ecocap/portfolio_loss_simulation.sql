DROP TABLE IF EXISTS PORTFOLIO_LOSS_SIMULATION;

CREATE TABLE PORTFOLIO_LOSS_SIMULATION
             ( 
                          ITERATIONID    INTEGER, 
                          PORTFOLIO_LOSS DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );