
DROP TABLE IF EXISTS DIM_BANK;
CREATE TABLE DIM_BANK(	

BANK_ID STRING,
SRC_BANK_ID STRING,
BANK_CODE STRING,
BANK_NAME STRING,
BANK_ACCOUNT_NUMBER STRING,
BANK_CURRENCY_CODE STRING,
BANK_CHECK_DIGITS INT
)

PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
