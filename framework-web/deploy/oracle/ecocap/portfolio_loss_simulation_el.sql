DROP TABLE PORTFOLIO_LOSS_SIMULATION_EL;

CREATE TABLE PORTFOLIO_LOSS_SIMULATION_EL
             ( 
                          ITERATIONID      INTEGER, 
                          PORTFOLIO_LOSS   DECIMAL(10,2), 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR2(50), 
                          VERSION          INTEGER 
             );
