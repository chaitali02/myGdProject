CREATE TABLE edw_small.dq_rule_results
(
  rowkey VARCHAR(50),
  datapoduuid VARCHAR(50),
  datapodversion VARCHAR(50),
  datapodname VARCHAR(50),
  attributeid VARCHAR(50),
  attributename VARCHAR(50),
  attributevalue VARCHAR(50),
  nullcheck_pass VARCHAR(50),
  valuecheck_pass VARCHAR(50),
  rangecheck_pass VARCHAR(50),
  datatypecheck_pass VARCHAR(50),
  dataformatcheck_pass VARCHAR(50),
  lengthcheck_pass VARCHAR(50),
  refintegritycheck_pass VARCHAR(50),
  dupcheck_pass VARCHAR(50),
  customcheck_pass VARCHAR(50),
  version integer
);
