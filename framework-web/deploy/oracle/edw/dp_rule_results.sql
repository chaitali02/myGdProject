create table "dp_rule_results"
(
  "datapoduuid" varchar2(70 byte),
  "datapodversion" varchar2(70 byte),
  "datapodname" varchar2(70 byte),
  "attributeid" varchar2(70 byte),
  "attributename" varchar2(70 byte),
  "numrows" varchar2(70 byte),
  "minval" number(30,0),
  "maxval" number(30,0),
  "avgval" number(30,0),
  "medianval" number(30,0),
  "stddev" number(30,0),
  "numdistinct" number(30,0),
  "perdistinct" number(30,0),
  "numnull" number(30,0),
  "pernull" number(30,0),
  "sixsigma" number(30,0),
  "load_date" varchar2(70 byte),
  "load_id" number(30,0),
  "version" varchar2(70 byte)
);
