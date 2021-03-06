DROP TABLE IF EXISTS ACCOUNT_POSTGRES;

CREATE TABLE ACCOUNT_POSTGRES
  ( 
     ACCOUNT_ID            VARCHAR(50) DEFAULT 0 NOT NULL,
     ACCOUNT_TYPE_ID       VARCHAR(50),
     ACCOUNT_STATUS_ID     VARCHAR(50),
     PRODUCT_TYPE_ID       VARCHAR(50),
     CUSTOMER_ID           VARCHAR(50),
     PIN_NUMBER            INTEGER,
     NATIONALITY           VARCHAR(50),
     PRIMARY_IDEN_DOC      VARCHAR(50),
     PRIMARY_IDEN_DOC_ID   VARCHAR(50),
     SECONDARY_IDEN_DOC    VARCHAR(50),
     SECONDARY_IDEN_DOC_ID VARCHAR(50),
     ACCOUNT_OPEN_DATE     VARCHAR(10),
     ACCOUNT_NUMBER        VARCHAR(50),
     OPENING_BALANCE       INTEGER,
     CURRENT_BALANCE       INTEGER,
     OVERDUE_BALANCE       INTEGER,
     OVERDUE_DATE          VARCHAR(10),
     CURRENCY_CODE         VARCHAR(10),
     INTEREST_TYPE         VARCHAR(10),
     INTEREST_RATE         DECIMAL(10,2),
     LOAD_DATE             VARCHAR(10),
     LOAD_ID               INTEGER,
     CONSTRAINT ACCOUNT_POSTGRES_PK PRIMARY KEY (ACCOUNT_ID) 
  );
