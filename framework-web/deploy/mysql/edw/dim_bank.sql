DROP TABLE IF EXISTS DIM_BANK;
CREATE TABLE DIM_BANK(	
	BANK_ID VARCHAR(50) DEFAULT 0 NOT NULL,
	SRC_BANK_ID VARCHAR(50),
	BANK_CODE VARCHAR(10),
	BANK_NAME VARCHAR(100),
	BANK_ACCOUNT_NUMBER VARCHAR(50),
	BANK_CURRENCY_CODE VARCHAR(50),
	BANK_CHECK_DIGITS INTEGER(20),
	LOAD_DATE VARCHAR(10),
	LOAD_ID INTEGER(50), 
CONSTRAINT BANK_ID_PK  PRIMARY KEY(BANK_ID));