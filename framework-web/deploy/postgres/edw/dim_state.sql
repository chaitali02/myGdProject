DROP TABLE IF EXISTS DIM_STATE; 

CREATE TABLE DIM_STATE 
  ( 
     STATE_ID         VARCHAR(50) DEFAULT 0 NOT NULL,
     STATE_CODE       VARCHAR(10),
     STATE_NAME       VARCHAR(100),
     COUNTRY_CODE     VARCHAR(10),
     STATE_POPULATION INTEGER,
     LOAD_DATE        VARCHAR(10),
     LOAD_ID          INTEGER,
     CONSTRAINT STATE_ID_DIM__PK PRIMARY KEY(STATE_ID) 
  );