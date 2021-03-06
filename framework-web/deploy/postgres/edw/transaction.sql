DROP TABLE IF EXISTS TRANSACTION; 

CREATE TABLE  TRANSACTION 
  ( 
     TRANSACTION_ID      VARCHAR(50) DEFAULT 0 NOT NULL,
     TRANSACTION_TYPE_ID VARCHAR(50),
     ACCOUNT_ID          VARCHAR(50),
     TRANSACTION_DATE    VARCHAR(10),
     FROM_ACCOUNT        VARCHAR(50),
     TO_ACCOUNT          VARCHAR(50),
     AMOUNT_BASE_CURR    DECIMAL(10,2),
     AMOUNT_USD          DECIMAL(10,2),
     CURRENCY_CODE       VARCHAR(10),
     CURRENCY_RATE       DECIMAL(10,2),
     NOTES               VARCHAR(100),
     LOAD_DATE           VARCHAR(10),
     LOAD_ID             INTEGER,
     CONSTRAINT TRANSACTION_ID_PK PRIMARY KEY(TRANSACTION_ID) 
  ); 