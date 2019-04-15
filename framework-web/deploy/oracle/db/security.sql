DROP TABLE IF EXISTS security; 

CREATE TABLE security 
  ( 
     security_id          INTEGER, 
     security_symbol      VARCHAR2(50), 
     ric_code             VARCHAR2(50), 
     security_description VARCHAR2(100), 
     version              INTEGER 
  ); 
