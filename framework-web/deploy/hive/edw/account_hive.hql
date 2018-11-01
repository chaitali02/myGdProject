DROP TABLE IF EXISTS ACCOUNT_HIVE; 

CREATE TABLE ACCOUNT_HIVE 
  ( 
     ACCOUNT_ID            STRING, 
     ACCOUNT_TYPE_ID       STRING, 
     ACCOUNT_STATUS_ID     STRING, 
     PRODUCT_TYPE_ID       STRING, 
     CUSTOMER_ID           STRING, 
     PIN_NUMBER            INT, 
     NATIONALITY           STRING, 
     PRIMARY_IDEN_DOC      STRING, 
     PRIMARY_IDEN_DOC_ID   STRING, 
     SECONDARY_IDEN_DOC    STRING, 
     SECONDARY_IDEN_DOC_ID STRING, 
     ACCOUNT_OPEN_DATE     STRING, 
     ACCOUNT_NUMBER        STRING, 
     OPENING_BALANCE       INT, 
     CURRENT_BALANCE       INT, 
     OVERDUE_BALANCE       INT, 
     OVERDUE_DATE          STRING, 
     CURRENCY_CODE         STRING, 
     INTEREST_TYPE         STRING, 
     INTEREST_RATE         DECIMAL,
     LOAD_DATE STRING,
     LOAD_ID STRING
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
