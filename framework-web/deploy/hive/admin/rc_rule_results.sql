DROP tableif EXISTS rc_rule_results; 

CREATE TABLE rc_rule_results 
  ( 
     sourceuuid          STRING, 
     sourceversion       STRING, 
     sourcename          STRING, 
     sourceattributeid   STRING, 
     sourceattributename STRING, 
     sourcevalue         DECIMAL, 
     targetuuid          STRING, 
     targetversion       STRING, 
     targetname          STRING, 
     targetattributeid   STRING, 
     targetattributename STRING, 
     targetvalue         DECIMAL, 
     status              STRING, 
     version             STRING
  ); 
