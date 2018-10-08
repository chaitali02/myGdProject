DROP TABLE IF EXISTS DIM_TRANSACTION_TYPE; 

CREATE TABLE DIM_TRANSACTION_TYPE 
  ( 
     TRANSACTION_TYPE_ID     VARCHAR(50) DEFAULT 0 NOT NULL,
     SRC_TRANSACTION_TYPE_ID VARCHAR(50),
     TRANSACTION_TYPE_CODE   VARCHAR(10),
     TRANSACTION_TYPE_DESC   VARCHAR(500),
     LOAD_DATE               VARCHAR(10),
     LOAD_ID                 INTEGER,
     CONSTRAINT TRANSACTION_TYPE_ID_DIM__PK PRIMARY KEY(TRANSACTION_TYPE_ID) 
  );