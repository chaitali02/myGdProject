DROP TABLE TRANSACTION_TYPE;
CREATE TABLE TRANSACTION_TYPE 
  ( 
     TRANSACTION_TYPE_ID   VARCHAR2(50) NOT NULL, 
     TRANSACTION_TYPE_CODE VARCHAR2(10), 
     TRANSACTION_TYPE_DESC VARCHAR2(500), 
     LOAD_DATE             VARCHAR2(10), 
     LOAD_ID               INTEGER, 
     CONSTRAINT TRANSACTION_TYPE_PK PRIMARY KEY (TRANSACTION_TYPE_ID) 
  ); 
