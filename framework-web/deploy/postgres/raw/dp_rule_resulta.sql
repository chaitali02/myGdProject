
-- Table: framework.dp_rule_results

   DROP TABLE framework.dp_rule_results;

CREATE TABLE framework.dp_rule_results (
    datapoduuid text,
    datapodversion text,
    datapodname text,
    attributeid text,
    attributename text,
    numrows text,
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
    load_date integer,
    load_id integer,
    version integer
);

