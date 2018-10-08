
CREATE TABLE BRANCH_TYPE 
  ( 
     BRANCH_TYPE_ID   VARCHAR2(50) NOT NULL, 
     BRANCH_TYPE_CODE VARCHAR2(10), 
     BRANCH_TYPE_DESC VARCHAR2(500), 
     LOAD_DATE        VARCHAR2(10), 
     LOAD_ID          INTEGER, 
     CONSTRAINT BRANCH_TYPE_PK PRIMARY KEY (BRANCH_TYPE_ID) 
  ); 

