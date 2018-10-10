
DROP TABLE  PORTFOLIO_LOSS_SIMULATION_AGGR;

CREATE TABLE  PORTFOLIO_LOSS_SIMULATION_AGGR
             ( 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
