 DROP TABLE CUSTOMER;
CREATE TABLE CUSTOMER 
  ( 
     CUSTOMER_ID            VARCHAR2(50) NOT NULL, 
     ADDRESS_ID             VARCHAR2(50), 
     BRANCH_ID              VARCHAR2(50), 
     TITLE                  VARCHAR2(100), 
     FIRST_NAME             VARCHAR2(100), 
     MIDDLE_NAME            VARCHAR2(100), 
     LAST_NAME              VARCHAR2(100), 
     SSN                    VARCHAR2(100), 
     PHONE                  VARCHAR2(100), 
     DATE_FIRST_PURCHASE    VARCHAR2(10), 
     COMMUTE_DISTANCE_MILES INTEGER, 
     LOAD_DATE              VARCHAR2(10), 
     LOAD_ID                INTEGER, 
     CONSTRAINT CUSTOMER_PK PRIMARY KEY (CUSTOMER_ID) 
  ); 
