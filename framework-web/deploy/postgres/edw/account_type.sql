
DROP TABLE EDW_SMALL.account_type;
CREATE TABLE EDW_SMALL.account_type(	ACCOUNT_TYPE_ID VARCHAR(50) DEFAULT 0 NOT NULL,
	ACCOUNT_TYPE_CODE VARCHAR(10),
	ACCOUNT_TYPE_DESC VARCHAR(500),
	LOAD_DATE VARCHAR(10),
	LOAD_ID INTEGER, 
CONSTRAINT ACCOUNT_TYPE_ID_PK  PRIMARY KEY(ACCOUNT_TYPE_ID,LOAD_DATE,LOAD_ID));
