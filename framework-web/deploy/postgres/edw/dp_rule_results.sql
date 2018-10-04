CREATE TABLE edw_small.dp_rule_results
(
  datapoduuid VARCHAR(50),
  datapodversion VARCHAR(50),
  datapodname VARCHAR(50),
  attributeid VARCHAR(50),
  attributename VARCHAR(50),
  numrows VARCHAR(50),
  minval double precision,
  maxval double precision,
  avgval double precision,
  medianval double precision,
  stddev double precision,
  numdistinct integer,
  perdistinct double precision,
  numnull integer,
  pernull double precision,
  sixsigma double precision,
  load_date VARCHAR(50),
  load_id integer,
  version integer
);


