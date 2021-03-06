DROP TABLE IF EXISTS PORTFOLIO_LOSS_AGGR_ES; 

CREATE TABLE PORTFOLIO_LOSS_AGGR_ES 
  ( 
     EXPECTED_LOSS    DECIMAL, 
     VALUE_AT_RISK    DECIMAL, 
     ECONOMIC_CAPITAL DECIMAL, 
     EXPECTED_SUM     DECIMAL, 
     REPORTING_DATE   STRING, 
     VERSION          INT 
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','; 