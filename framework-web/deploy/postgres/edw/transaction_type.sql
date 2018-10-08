DROP TABLE IF EXISTS TRANSACTION_TYPE; 

CREATE TABLE TRANSACTION_TYPE 
  ( 
     TRANSACTION_TYPE_ID   VARCHAR(50) DEFAULT 0 NOT NULL,
     TRANSACTION_TYPE_CODE VARCHAR(10),
     TRANSACTION_TYPE_DESC VARCHAR(500),
     LOAD_DATE             VARCHAR(10),
     LOAD_ID               INTEGER,
     CONSTRAINT TRANSACTION_TYPE_ID_PK PRIMARY KEY(TRANSACTION_TYPE_ID) 
  ); 