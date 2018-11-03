
DROP TABLE IF EXISTS FACT_TRANSACTION_JOURNAL;
CREATE TABLE FACT_TRANSACTION_JOURNAL 
(
  TRANSACTION_ID VARCHAR(50),
  DIRECTION VARCHAR(50), 
  ACCOUNT_ID VARCHAR(50), 
  CUSTOMER_ID VARCHAR(50), 
  TRANSACTION_TYPE_CODE VARCHAR(50), 
  TRANSACTION_DATE VARCHAR(50), 
  TRANSACTION_COUNTRY VARCHAR(50), 
  SENDER_COUNTRY VARCHAR(50), 
  RECIEVER_COUNTRY VARCHAR(50), 
  AMOUNT_USD DECIMAL(10,3), 
  LOAD_DATE VARCHAR(50), 
  LOAD_ID INTEGER(50), 
CONSTRAINT TRANSACTION_ID_PK PRIMARY KEY(TRANSACTION_ID));

