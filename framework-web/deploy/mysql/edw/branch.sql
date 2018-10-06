
DROP TABLE BRANCH;
CREATE TABLE BRANCH(	
	BRANCH_ID VARCHAR(50) DEFAULT 0 NOT NULL,
	BRANCH_TYPE_ID VARCHAR(50),
	BANK_ID VARCHAR(50),
	ADDRESS_ID VARCHAR(50),
	BRANCH_NAME VARCHAR(100),
	BRANCH_DESC VARCHAR(500),
	BRANCH_CONTACT_NAME VARCHAR(100),
	BRANCH_CONTACT_PHONE VARCHAR(100),
	BRANCH_CONTACT_EMAIL VARCHAR(100),
	LOAD_DATE VARCHAR(10),
	LOAD_ID INTEGER(50), 
CONSTRAINT BRANCH_ID_PK  PRIMARY KEY(BRANCH_ID,LOAD_DATE,LOAD_ID));
ALTER TABLE BRANCH PARTITION BY KEY(LOAD_DATE,LOAD_ID);

