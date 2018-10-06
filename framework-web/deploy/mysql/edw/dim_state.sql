DROP TABLE IF EXISTS DIM_STATE;
CREATE TABLE DIM_STATE(	
	STATE_ID VARCHAR(50) DEFAULT 0 NOT NULL,
	STATE_CODE VARCHAR(10),
	STATE_NAME VARCHAR(100),
	COUNTRY_CODE VARCHAR(10),
	STATE_POPULATION INTEGER(10),
	LOAD_DATE VARCHAR(10),
	LOAD_ID INTEGER(50), 
CONSTRAINT STATE_ID_PK  PRIMARY KEY(STATE_ID));