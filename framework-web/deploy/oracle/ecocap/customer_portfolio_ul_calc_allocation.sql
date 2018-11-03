DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION
             ( 
                          CUST_ID                      VARCHAR2(50), 
                          PORTFOLIO_UL_CUST_ALLOCATION DECIMAL(10,2), 
                          REPORTING_DATE               VARCHAR2(50), 
                          VERSION                      INTEGER 
             );
