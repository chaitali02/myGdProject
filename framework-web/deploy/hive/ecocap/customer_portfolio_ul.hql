DROP TABLE IF EXISTS CUSTOMER_PORTFOLIO_UL; 

CREATE TABLE CUSTOMER_PORTFOLIO_UL 
  ( 
     CUST_ID          STRING, 
     INDUSTRY         STRING, 
     PD               DECIMAL, 
     EXPOSURE         INT, 
     LGD              DECIMAL, 
     LGD_VAR          INT, 
     CORRELATION      DECIMAL, 
     SQRT_CORRELATION DECIMAL, 
     DEF_POINT        DECIMAL, 
     UNEXPECTED_LOSS  DECIMAL, 
     REPORTING_DATE   STRING, 
     VERSION          INT 
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','; 