drop table if exists rc_rule_results;
create table rc_rule_results (
    "sourcedatapoduuid"  varchar2(70 byte),
    "sourcedatapodversion"  varchar2(70 byte),
    "sourcedatapodname"  varchar2(70 byte),
    "sourceattributeid"  varchar2(70 byte),
    "sourceattributename"  varchar2(70 byte),
    "sourcevalue"  number(30,0),
    "targetdatapoduuid"  varchar2(70 byte),
    "targetdatapodversion"  varchar2(70 byte),
    "targetdatapodname"  varchar2(70 byte),
    "targetattributeid"  varchar2(70 byte),
    "targetattributename"  varchar2(70 byte),
    "targetvalue"  number(30,0),
    "status"  varchar2(70 byte)
);
