DROP TABLE framework.rc_rule_results;

CREATE TABLE framework.rc_rule_results (
    sourcedatapoduuid text,
    sourcedatapodversion text,
    sourcedatapodname text,
    sourceattributeid text,
    sourceattributename text,
    targetdatapoduuid text,
    targetdatapodversion text,
    targetdatapodname text,
    targetattributeid text,
    targetattributename text,
    sourcevalue double precision,
    targetvalue double precision,
    status text,
    version integer
);

