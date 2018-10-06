DROP TABLE IF EXISTS DP_RULE_RESULTS;
CREATE TABLE DP_RULE_RESULTS(
DATAPODUUID STRING,
DATAPODVERSION STRING,
DATAPODNAME STRING,
ATTRIBUTEID STRING,
ATTRIBUTENAME STRING,
NUMROWS STRING,
MINVAL DECIMAL,
MAXVAL DECIMAL,
AVGVAL DECIMAL,
MEDIANVAL DECIMAL,
STDDEV DECIMAL,
NUMDISTINCT INT,
PERDISTINCT DECIMAL,
NUMNULL INT,
PERNULL DECIMAL,
SIXSIGMA DECIMAL,
VERSION INT
);
