DROP table  rc_rule_results; 

CREATE TABLE rc_rule_results 
  ( 
     sourceuuid          VARCHAR2(50), 
     sourceversion       VARCHAR2(50), 
     sourcename          VARCHAR2(50), 
     sourceattributeid   VARCHAR2(50), 
     sourceattributename VARCHAR2(50), 
     sourcevalue         DECIMAL(10, 4), 
     targetuuid          VARCHAR2(50), 
     targetversion       VARCHAR2(50), 
     targetname          VARCHAR2(50), 
     targetattributeid   VARCHAR2(50), 
     targetattributename VARCHAR2(50), 
     targetvalue         DECIMAL(10, 4), 
     status              VARCHAR2(50), 
     version             VARCHAR2(50) 
  ); 
