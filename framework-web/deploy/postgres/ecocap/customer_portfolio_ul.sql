DROP TABLE IF EXISTS CUSTOMER_PORTFOLIO_UL;

CREATE TABLE CUSTOMER_PORTFOLIO_UL 
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          UNEXPECTED_LOSS  DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );