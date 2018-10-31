DROP TABLE IF EXISTS FACT_TRANSACTION_JOURNAL;

CREATE TABLE FACT_TRANSACTION_JOURNAL 
(
  TRANSACTION_ID STRING,
  DIRECTION STRING, 
  ACCOUNT_ID STRING, 
  CUSTOMER_ID STRING, 
  TRANSACTION_TYPE_CODE STRING, 
  TRANSACTION_DATE STRING, 
  TRANSACTION_COUNTRY STRING, 
  SENDER_COUNTRY STRING, 
  RECIEVER_COUNTRY STRING, 
  AMOUNT_USD DECIMAL, 
  LOAD_DATE STRING, 
  LOAD_ID INTEGER
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

