DROP TABLE CUSTOMER_ES_ALLOCATION;

CREATE TABLE CUSTOMER_ES_ALLOCATION 
             ( 
                          CUST_ID         VARCHAR2(50), 
                          ES_CONTRIBUTION DECIMAL(10,2), 
                          ES_ALLOCATION   DECIMAL(10,2), 
                          REPORTING_DATE  VARCHAR2(50), 
                          VERSION         INTEGER 
             );
