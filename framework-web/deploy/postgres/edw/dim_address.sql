DROP TABLE IF EXISTS DIM_ADDRESS; 

CREATE TABLE DIM_ADDRESS 
  ( 
     ADDRESS_ID     VARCHAR(50) DEFAULT 0 NOT NULL,
     SRC_ADDRESS_ID VARCHAR(50),
     ADDRESS_LINE1  VARCHAR(50),
     ADDRESS_LINE2  VARCHAR(50),
     ADDRESS_LINE3  VARCHAR(50),
     CITY           VARCHAR(100),
     COUNTY         VARCHAR(100),
     STATE          VARCHAR(100),
     ZIPCODE        INTEGER,
     COUNTRY        VARCHAR(100),
     LATITUDE       VARCHAR(50),
     LONGTITUDE     VARCHAR(50),
     LOAD_DATE      VARCHAR(10),
     LOAD_ID        INTEGER,
     CONSTRAINT ADDRESS_ID_DIM__PK PRIMARY KEY(ADDRESS_ID) 
  );
