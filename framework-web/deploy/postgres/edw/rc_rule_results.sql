DROP TABLE rc_rule_results;

CREATE TABLE rc_rule_results (
    sourcedatapoduuid text,
    sourcedatapodversion text,
    sourcedatapodname text,
    sourceattributeid text,
    sourceattributename text,
    sourcevalue double precision,
    targetdatapoduuid text,
    targetdatapodversion text,
    targetdatapodname text,
    targetattributeid text,
    targetattributename text,
    targetvalue double precision,
    status text,
    version integer
);

