DROP TABLE CUSTOMER_PORTFOLIO_CLONE;

CREATE TABLE CUSTOMER_PORTFOLIO_CLONE
             ( 
                          CUST_ID          VARCHAR2(50), 
                          INDUSTRY         VARCHAR2(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR2(50), 
                          VERSION          INTEGER 
             );
