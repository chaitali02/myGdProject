DROP tableif EXISTS rc_rule_results; 

CREATE TABLE rc_rule_results 
  ( 
     sourceuuid          VARCHAR(50), 
     sourceversion       VARCHAR(50), 
     sourcename          VARCHAR(50), 
     sourceattributeid   VARCHAR(50), 
     sourceattributename VARCHAR(50), 
     sourcevalue         DECIMAL(10, 4), 
     targetuuid          VARCHAR(50), 
     targetversion       VARCHAR(50), 
     targetname          VARCHAR(50), 
     targetattributeid   VARCHAR(50), 
     targetattributename VARCHAR(50), 
     targetvalue         DECIMAL(10, 4), 
     status              VARCHAR(50), 
     version             VARCHAR(50) 
  ); 
