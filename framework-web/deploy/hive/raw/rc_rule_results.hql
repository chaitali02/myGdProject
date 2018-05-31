CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS rc_rule_results;
CREATE TABLE rc_rule_results (
    `sourcedatapoduuid` string,
    `sourcedatapodversion` string,
    `sourcedatapodname` string,
    `sourceattributeid` string,
    `sourceattributename` string,
    `targetdatapoduuid` string,
    `targetdatapodversion` string,
    `targetdatapodname` string,
    `targetattributeid` string,
    `targetattributename` string,
    `sourcevalue` BIGINT,
    `targetvalue` BIGINT,
    `status` string,
    `version` int
);

