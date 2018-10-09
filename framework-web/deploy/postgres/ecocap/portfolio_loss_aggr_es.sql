DROP TABLE IF EXISTS PORTFOLIO_LOSS_AGGR_ES;

CREATE TABLE PORTFOLIO_LOSS_AGGR_ES 
             ( 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          EXPECTED_SUM     DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );