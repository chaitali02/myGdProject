
CREATE TABLE DIM_ADDRESS 
  ( 
     ADDRESS_ID     VARCHAR2(50) NOT NULL, 
     SRC_ADDRESS_ID VARCHAR2(50), 
     ADDRESS_LINE1  VARCHAR2(50), 
     ADDRESS_LINE2  VARCHAR2(50), 
     ADDRESS_LINE3  VARCHAR2(50), 
     CITY           VARCHAR2(100), 
     COUNTY         VARCHAR2(100), 
     STATE          VARCHAR2(100), 
     ZIPCODE        INTEGER, 
     COUNTRY        VARCHAR2(100), 
     LATITUDE       VARCHAR2(50), 
     LONGTITUDE     VARCHAR2(50), 
     LOAD_DATE      VARCHAR2(10), 
     LOAD_ID        INTEGER, 
     CONSTRAINT DIM_ADDRESS_PK PRIMARY KEY (ADDRESS_ID) 
  ); 
