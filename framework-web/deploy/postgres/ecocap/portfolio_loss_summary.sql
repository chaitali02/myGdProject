DROP TABLE IF EXISTS PORTFOLIO_LOSS_SUMMARY;

CREATE TABLE PORTFOLIO_LOSS_SUMMARY 
             ( 
                          PORTFOLIO_AVG_PD           DECIMAL(10,2), 
                          PORTFOLIO_AVG_LGD          DECIMAL(10,2), 
                          PORTFOLIO_TOTAL_EAD        DECIMAL(10,2), 
                          PORTFOLIO_EXPECTED_LOSS    DECIMAL(10,2), 
                          PORTFOLIO_VALUE_AT_RISK    DECIMAL(10,2), 
                          PORTFOLIO_ECONOMIC_CAPITAL DECIMAL(10,2), 
                          PORTFOLIO_EXPECTED_SUM     DECIMAL(10,2), 
                          PORTFOLIO_ES_PERCENTAGE    DECIMAL(10,2), 
                          PORTFOLIO_VAL_PERCENTAGE   DECIMAL(10,2), 
                          PORTFOLIO_EL_PERCENTAGE    DECIMAL(10,2), 
                          PORTFOLIO_EC_PERCENTAGE    DECIMAL(10,2), 
                          REPORTING_DATE             VARCHAR(50), 
                          VERSION                    INTEGER 
             );
