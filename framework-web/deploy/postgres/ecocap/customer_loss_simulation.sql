DROP TABLE IF EXISTS CUSTOMER_LOSS_SIMULATION;

CREATE TABLE CUSTOMER_LOSS_SIMULATION
             ( 
                          CUST_ID        VARCHAR(50), 
                          ITERATIONID    INTEGER, 
                          CUSTOMER_LOSS  DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
