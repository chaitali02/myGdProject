DROP TABLE IF EXISTS FACT_TRANSACTION;
CREATE TABLE FACT_TRANSACTION(	
	TRANSACTION_ID VARCHAR(50) DEFAULT 0 NOT NULL,
	SRC_TRANSACTION_ID VARCHAR(50),
	TRANSACTION_TYPE_ID INTEGER(50),
	TRANS_DATE_ID INTEGER(50),
	BANK_ID INTEGER(50),
	BRANCH_ID INTEGER(50),
	CUSTOMER_ID VARCHAR(50),
	ADDRESS_ID VARCHAR(50),
	ACCOUNT_ID VARCHAR(50),
	FROM_ACCOUNT VARCHAR(50),
	TO_ACCOUNT VARCHAR(50),
	AMOUNT_BASE_CURR INTEGER(10),
	AMOUNT_USD INTEGER(10),
	CURRENCY_CODE VARCHAR(10),
	CURRENCY_RATE INTEGER(10),
	NOTES VARCHAR(50),
	LOAD_DATE VARCHAR(10),
	LOAD_ID INTEGER(50), 
CONSTRAINT TRANSACTION_ID_PK  PRIMARY KEY(TRANSACTION_ID));