DROP TABLE IF EXISTS CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY
             ( 
                          CUST_ID                VARCHAR(50), 
                          PORTFOLIO_UL_CUST_SUM  DECIMAL(10,2), 
                          PORTFOLIO_UL_TOTAL_SUM DECIMAL(10,2), 
                          REPORTING_DATE         VARCHAR(50), 
                          VERSION                INTEGER 
             );
