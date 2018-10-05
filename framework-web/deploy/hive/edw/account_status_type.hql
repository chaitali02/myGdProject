

DROP TABLE IF EXISTS ACCOUNT_STATUS_TYPE;
CREATE TABLE ACCOUNT_STATUS_TYPE(	

ACCOUNT_STATUS_ID STRING,
ACCOUNT_STATUS_CODE STRING,
ACCOUNT_STATUS_DESC STRING
)

PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
