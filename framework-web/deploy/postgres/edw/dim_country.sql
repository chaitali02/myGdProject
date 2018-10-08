DROP TABLE IF EXISTS DIM_COUNTRY; 

CREATE TABLE DIM_COUNTRY 
  ( 
     COUNTRY_ID         VARCHAR(50) DEFAULT 0 NOT NULL,
     COUNTRY_CODE       VARCHAR(10),
     COUNTRY_NAME       VARCHAR(100),
     COUNTRY_POPULATION INTEGER,
     LOAD_DATE          VARCHAR(10),
     LOAD_ID            INTEGER,
     CONSTRAINT COUNTRY_ID_DIM__PK PRIMARY KEY(COUNTRY_ID) 
  );
