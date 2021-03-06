DROP TABLE IF EXISTS CUSTOMER; 

CREATE TABLE CUSTOMER 
  ( 
     CUSTOMER_ID            VARCHAR(50) DEFAULT 0 NOT NULL,
     ADDRESS_ID             VARCHAR(50),
     BRANCH_ID              VARCHAR(50),
     TITLE                  VARCHAR(100),
     FIRST_NAME             VARCHAR(100),
     MIDDLE_NAME            VARCHAR(100),
     LAST_NAME              VARCHAR(100),
     SSN                    VARCHAR(100),
     PHONE                  VARCHAR(100),
     DATE_FIRST_PURCHASE    VARCHAR(10),
     COMMUTE_DISTANCE_MILES INTEGER,
     LOAD_DATE              VARCHAR(10),
     LOAD_ID                INTEGER,
     CONSTRAINT CUSTOMER_ID_PK PRIMARY KEY(CUSTOMER_ID) 
  );
