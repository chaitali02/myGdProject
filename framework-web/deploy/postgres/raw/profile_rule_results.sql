

-- Table: framework.profile_rule_results

   DROP TABLE framework.profile_rule_results;
   
CREATE TABLE framework.profile_rule_results (
  DatapodUUID text ,
  DatapodVersion text ,
  AttributeId text ,
  minVal double precision ,
  maxVal double precision ,
  avgVal double precision ,
  medianVal double precision ,
  stdDev double precision ,
  numDistinct integer ,
  perDistinct double precision ,
  numNull integer ,
  perNull double precision ,
  sixSigma double precision ,
  version integer 
);
 


