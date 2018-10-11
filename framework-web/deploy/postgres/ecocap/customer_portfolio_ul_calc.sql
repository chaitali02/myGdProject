DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC
             ( 
                          CUST_ID1          VARCHAR(50), 
                          INDUSTRY1         VARCHAR(50), 
                          CORRELATION1      DECIMAL(10,2), 
                          UNEXPECTED_LOSS1  DECIMAL(10,2), 
                          CUST_ID2          VARCHAR(50), 
                          INDUSTRY2         VARCHAR(50), 
                          CORRELATION2      DECIMAL(10,2), 
                          UNEXPECTED_LOSS2  DECIMAL(10,2), 
                          FACTOR_VALUE      DECIMAL(10,2), 
                          PORTFOLIO_UL_CALC DECIMAL(10,2), 
                          REPORTING_DATE    VARCHAR(50), 
                          VERSION           INTEGER 
             );
