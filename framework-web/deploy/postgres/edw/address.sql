
DROP TABLE EDW_SMALL.address;
CREATE TABLE EDW_SMALL.address(	ADDRESS_ID VARCHAR(50) DEFAULT 0 NOT NULL,
	ADDRESS_LINE1 VARCHAR(50),
	ADDRESS_LINE2 VARCHAR(50),
	ADDRESS_LINE3 VARCHAR(50),
	CITY VARCHAR(100),
	COUNTY VARCHAR(100),
	STATE VARCHAR(100),
	ZIPCODE INTEGER,
	COUNTRY VARCHAR(100),
	LATITUDE VARCHAR(50),
	LONGITUDE VARCHAR(50),
	LOAD_DATE VARCHAR(10),
	LOAD_ID INTEGER, 
CONSTRAINT ADDRESS_ID_PK  PRIMARY KEY(ADDRESS_ID,LOAD_DATE,LOAD_ID));
