
DROP TABLE IF EXISTS RC_RULE_RESULTS;
CREATE TABLE RC_RULE_RESULTS(	

SOURCEUUID STRING,
SOURCEVERSION STRING,
SOURCENAME STRING,
SOURCEATTRIBUTEID STRING,
SOURCEATTRIBUTENAME STRING,
SOURCEVALUE BIGDECIMAL,
TARGETUUID STRING,
TARGETVERSION STRING,
TARGETNAME STRING,
TARGETATTRIBUTEID STRING,
TARGETATTRIBUTENAME STRING,
TARGETVALUE BIGDECIMAL,
STATUS STRING,
VERSION INT

);
