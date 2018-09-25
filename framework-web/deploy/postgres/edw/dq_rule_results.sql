DROP TABLE dq_rule_results;

CREATE TABLE dq_rule_results
(
  rowkey text,
  datapoduuid text,
  datapodversion text,
  datapodname text,
  attributeid text,
  attributename text,
  attributevalue text,
  nullcheck_pass text,
  valuecheck_pass text,
  rangecheck_pass text,
  datatypecheck_pass text,
  dataformatcheck_pass text,
  lengthcheck_pass text,
  refintegritycheck_pass text,
  dupcheck_pass text,
  customcheck_pass text,
  version integer
);
