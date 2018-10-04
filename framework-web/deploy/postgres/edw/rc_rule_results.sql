CREATE TABLE edw_small.rc_rule_results (
    sourcedatapoduuid VARCHAR(50),
    sourcedatapodversion VARCHAR(50),
    sourcedatapodname VARCHAR(50),
    sourceattributeid VARCHAR(50),
    sourceattributename VARCHAR(50),
    sourcevalue double precision,
    targetdatapoduuid VARCHAR(50),
    targetdatapodversion VARCHAR(50),
    targetdatapodname VARCHAR(50),
    targetattributeid VARCHAR(50),
    targetattributename VARCHAR(50),
    targetvalue double precision,
    status VARCHAR(50),
    version integer
);
