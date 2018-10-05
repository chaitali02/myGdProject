
DROP TABLE IF EXISTS FACT_TRANSACTION;
CREATE TABLE FACT_TRANSACTION(	

TRANSACTION_ID STRING,
SRC_TRANSACTION_ID STRING,
TRANSACTION_TYPE_ID INT,
TRANS_DATE_ID INT,
BANK_ID INT,
BRANCH_ID INT,
CUSTOMER_ID STRING,
ADDRESS_ID STRING,
ACCOUNT_ID STRING,
FROM_ACCOUNT STRING,
TO_ACCOUNT STRING,
AMOUNT_BASE_CURR INT,
AMOUNT_USD INT,
CURRENCY_CODE STRING,
CURRENCY_RATE INT,
NOTES STRING
)

PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

