DROP TABLE DIM_COUNTRY;
CREATE TABLE DIM_COUNTRY 
  ( 
     COUNTRY_ID         VARCHAR2(50) NOT NULL, 
     COUNTRY_CODE       VARCHAR2(10), 
     COUNTRY_NAME       VARCHAR2(100), 
     COUNTRY_POPULATION INTEGER, 
     LOAD_DATE          VARCHAR2(10), 
     LOAD_ID            INTEGER, 
     CONSTRAINT DIM_COUNTRY_PK PRIMARY KEY (COUNTRY_ID) 
  ); 
