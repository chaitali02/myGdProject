DROP TABLE IF EXISTS DIM_COUNTRY; 

CREATE TABLE DIM_COUNTRY 
  ( 
     COUNTRY_CODE       VARCHAR(10),
     COUNTRY_NAME       VARCHAR(100),
     COUNTRY_POPULATION INTEGER,
     COUNTRY_RISK_LEVEL INTEGER,
     LOAD_DATE          VARCHAR(10),
     LOAD_ID            INTEGER
  );
