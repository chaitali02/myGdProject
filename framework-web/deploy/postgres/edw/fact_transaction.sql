DROP TABLE IF EXISTS FACT_TRANSACTION; 

CREATE TABLE FACT_TRANSACTION 
  ( 
     TRANSACTION_ID      VARCHAR(50) DEFAULT 0 NOT NULL,
     SRC_TRANSACTION_ID  VARCHAR(50),
     TRANSACTION_TYPE_ID VARCHAR(50),
     TRANS_DATE_ID       VARCHAR(50),
     BANK_ID             VARCHAR(50),
     BRANCH_ID           VARCHAR(50),
     CUSTOMER_ID         VARCHAR(50),
     ADDRESS_ID          VARCHAR(50),
     ACCOUNT_ID          VARCHAR(50),
     FROM_ACCOUNT        VARCHAR(50),
     TO_ACCOUNT          VARCHAR(50),
     AMOUNT_BASE_CURR    INTEGER,
     AMOUNT_USD          INTEGER,
     CURRENCY_CODE       VARCHAR(10),
     CURRENCY_RATE       INTEGER,
     NOTES               VARCHAR(100),
     LOAD_DATE           VARCHAR(10),
     LOAD_ID             INTEGER,
     CONSTRAINT TRANSACTION_ID_FACT_PK PRIMARY KEY(TRANSACTION_ID) 
  );
