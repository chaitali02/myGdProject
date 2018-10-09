DROP TABLE DQ_RULE_RESULTS;
CREATE TABLE DQ_RULE_RESULTS 
  ( 
     ROWKEY                 VARCHAR2(50), 
     DATAPODUUID            VARCHAR2(50), 
     DATAPODVERSION         VARCHAR2(50), 
     DATAPODNAME            VARCHAR2(100), 
     ATTRIBUTEID            VARCHAR2(50), 
     ATTRIBUTENAME          VARCHAR2(100), 
     ATTRIBUTEVALUE         VARCHAR2(50), 
     NULLCHECK_PASS         VARCHAR2(50), 
     VALUECHECK_PASS        VARCHAR2(50), 
     RANGECHECK_PASS        VARCHAR2(50), 
     DATATYPECHECK_PASS     VARCHAR2(50), 
     DATAFORMATCHECK_PASS   VARCHAR2(50), 
     LENGTHCHECK_PASS       VARCHAR2(50), 
     REFINTEGRITYCHECK_PASS VARCHAR2(50), 
     DUPCHECK_PASS          VARCHAR2(50), 
     CUSTOMCHECK_PASS       VARCHAR2(50), 
     VERSION                INTEGER 
  ); 

