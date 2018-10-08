
CREATE TABLE FACT_CUSTOMER_SUMMARY_MONTHLY 
  ( 
     CUSTOMER_ID            VARCHAR2(50) NOT NULL, 
     YYYY_MM                VARCHAR2(50) NOT NULL, 
     TOTAL_TRANS_COUNT      VARCHAR2(50), 
     TOTAL_TRANS_AMOUNT_USD INTEGER, 
     AVG_TRANS_AMOUNT       INTEGER, 
     MIN_AMOUNT             DECIMAL(10, 2), 
     MAX_AMOUNT             DECIMAL(10, 2), 
     LOAD_DATE              VARCHAR2(10), 
     LOAD_ID                INTEGER, 
     CONSTRAINT FACT_CUSTOMER_SUMMARY_MONTHLY_PK PRIMARY KEY (CUSTOMER_ID) 
  ); 
