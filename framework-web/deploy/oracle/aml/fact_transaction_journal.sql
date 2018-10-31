DROP TABLE FACT_TRANSACTION_JOURNAL;
CREATE TABLE FACT_TRANSACTION_JOURNAL 
  ( 
     TRANSACTION_ID      	VARCHAR2(50) NOT NULL, 
     DIRECTION	         	VARCHAR2(50), 
     account_id 		VARCHAR2(50), 
     CUSTOMER_ID       	 	VARCHAR2(50), 
     transaction_type_code      VARCHAR2(50), 
     transaction_date           VARCHAR2(50), 
     transaction_country        VARCHAR2(50), 
     sender_country          	VARCHAR2(50), 
     reciever_country           VARCHAR2(50), 
     amount_usd                 DECIMAL(10, 2), 
     LOAD_DATE                  VARCHAR2(10), 
     LOAD_ID                    INTEGER, 
     CONSTRAINT FACT_TRANSACTION_PK PRIMARY KEY (TRANSACTION_ID) 
  ); 
