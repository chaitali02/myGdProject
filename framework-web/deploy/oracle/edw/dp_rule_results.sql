DROP TABLE DP_RULE_RESULTS;
CREATE TABLE DP_RULE_RESULTS 
  ( 
     DATAPODUUID    VARCHAR2(50) NOT NULL, 
     DATAPODVERSION VARCHAR2(50), 
     DATAPODNAME    VARCHAR2(100), 
     ATTRIBUTEID    VARCHAR2(50), 
     ATTRIBUTENAME  VARCHAR2(100), 
     NUMROWS        VARCHAR2(50), 
     MINVAL         DECIMAL(10, 2), 
     MAXVAL         DECIMAL(10, 2), 
     AVGVAL         DECIMAL(10, 3), 
     MEDIANVAL      DECIMAL(10, 3), 
     STDDEV         DECIMAL(10, 4), 
     NUMDISTINCT    INTEGER, 
     PERDISTINCT    DECIMAL(10, 2), 
     NUMNULL        INTEGER, 
     PERNULL        DECIMAL(10, 2), 
     SIXSIGMA       DECIMAL(10, 2), 
     VERSION        INTEGER 
  ); 
