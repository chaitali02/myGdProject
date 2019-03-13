DROP TABLE IF EXISTS security; 

CREATE TABLE security 
  ( 
     security_id          INT(10), 
     security_symbol      VARCHAR(50), 
     ric_code             VARCHAR(50), 
     security_description VARCHAR(100), 
     version              INT(10) 
  ); 