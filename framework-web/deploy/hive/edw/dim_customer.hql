
DROP TABLE IF EXISTS DIM_CUSTOMER;
CREATE TABLE DIM_CUSTOMER(	

CUSTOMER_ID STRING,
SRC_CUSTOMER_ID STRING,
TITLE STRING,
FIRST_NAME STRING,
MIDDLE_NAME STRING,
LAST_NAME STRING,
ADDRESS_LINE1 STRING,
ADDRESS_LINE2 STRING,
PHONE STRING,
DATE_FIRST_PURCHASE STRING,
COMMUTE_DISTANCE INT,
CITY STRING,
STATE STRING,
POSTAL_CODE STRING,
COUNTRY STRING
)

PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
