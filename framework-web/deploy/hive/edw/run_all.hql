DROP TABLE IF EXISTS ACCOUNT_HIVE; 

CREATE TABLE ACCOUNT_HIVE 
  ( 
     ACCOUNT_ID            STRING, 
     ACCOUNT_TYPE_ID       STRING, 
     ACCOUNT_STATUS_ID     STRING, 
     PRODUCT_TYPE_ID       STRING, 
     CUSTOMER_ID           STRING, 
     PIN_NUMBER            INT, 
     NATIONALITY           STRING, 
     PRIMARY_IDEN_DOC      STRING, 
     PRIMARY_IDEN_DOC_ID   STRING, 
     SECONDARY_IDEN_DOC    STRING, 
     SECONDARY_IDEN_DOC_ID STRING, 
     ACCOUNT_OPEN_DATE     STRING, 
     ACCOUNT_NUMBER        STRING, 
     OPENING_BALANCE       INT, 
     CURRENT_BALANCE       INT, 
     OVERDUE_BALANCE       INT, 
     OVERDUE_DATE          STRING, 
     CURRENCY_CODE         STRING, 
     INTEREST_TYPE         STRING, 
     INTEREST_RATE         DECIMAL 
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS ACCOUNT; 

CREATE TABLE ACCOUNT 
  ( 
     ACCOUNT_ID            STRING, 
     ACCOUNT_TYPE_ID       STRING, 
     ACCOUNT_STATUS_ID     STRING, 
     PRODUCT_TYPE_ID       STRING, 
     CUSTOMER_ID           STRING, 
     PIN_NUMBER            INT, 
     NATIONALITY           STRING, 
     PRIMARY_IDEN_DOC      STRING, 
     PRIMARY_IDEN_DOC_ID   STRING, 
     SECONDARY_IDEN_DOC    STRING, 
     SECONDARY_IDEN_DOC_ID STRING, 
     ACCOUNT_OPEN_DATE     STRING, 
     ACCOUNT_NUMBER        STRING, 
     OPENING_BALANCE       INT, 
     CURRENT_BALANCE       INT, 
     OVERDUE_BALANCE       INT, 
     OVERDUE_DATE          STRING, 
     CURRENCY_CODE         STRING, 
     INTEREST_TYPE         STRING, 
     INTEREST_RATE         DECIMAL 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS ACCOUNT_STATUS_TYPE; 

CREATE TABLE ACCOUNT_STATUS_TYPE 
  ( 
     ACCOUNT_STATUS_ID   STRING, 
     ACCOUNT_STATUS_CODE STRING, 
     ACCOUNT_STATUS_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS ACCOUNT_TYPE; 

CREATE TABLE ACCOUNT_TYPE 
  ( 
     ACCOUNT_TYPE_ID   STRING, 
     ACCOUNT_TYPE_CODE STRING, 
     ACCOUNT_TYPE_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS ADDRESS; 

CREATE TABLE ADDRESS 
  ( 
     ADDRESS_ID    STRING, 
     ADDRESS_LINE1 STRING, 
     ADDRESS_LINE2 STRING, 
     ADDRESS_LINE3 STRING, 
     CITY          STRING, 
     COUNTY        STRING, 
     STATE         STRING, 
     ZIPCODE       INT, 
     COUNTRY       STRING, 
     LATITUDE      STRING, 
     LONGITUDE     STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS BANK; 

CREATE TABLE BANK 
  ( 
     BANK_ID             STRING, 
     BANK_CODE           STRING, 
     BANK_NAME           STRING, 
     BANK_ACCOUNT_NUMBER STRING, 
     BANK_CURRENCY_CODE  STRING, 
     BANK_CHECK_DIGITS   INT 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS BRANCH; 

CREATE TABLE BRANCH 
  ( 
     BRANCH_ID            STRING, 
     BRANCH_TYPE_ID       STRING, 
     BANK_ID              STRING, 
     ADDRESS_ID           STRING, 
     BRANCH_NAME          STRING, 
     BRANCH_DESC          STRING, 
     BRANCH_CONTACT_NAME  STRING, 
     BRANCH_CONTACT_PHONE STRING, 
     BRANCH_CONTACT_EMAIL STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS BRANCH_TYPE; 

CREATE TABLE BRANCH_TYPE 
  ( 
     BRANCH_TYPE_ID   STRING, 
     BRANCH_TYPE_CODE STRING, 
     BRANCH_TYPE_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS CUSTOMER; 

CREATE TABLE CUSTOMER 
  ( 
     CUSTOMER_ID            STRING, 
     ADDRESS_ID             STRING, 
     BRANCH_ID              STRING, 
     TITLE                  STRING, 
     FIRST_NAME             STRING, 
     MIDDLE_NAME            STRING, 
     LAST_NAME              STRING, 
     SSN                    STRING, 
     PHONE                  STRING, 
     DATE_FIRST_PURCHASE    STRING, 
     COMMUTE_DISTANCE_MILES INT 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DIM_ACCOUNT; 

CREATE TABLE DIM_ACCOUNT 
  ( 
     ACCOUNT_ID            STRING, 
     SRC_ACCOUNT_ID        STRING, 
     ACCOUNT_TYPE_CODE     STRING, 
     ACCOUNT_STATUS_CODE   STRING, 
     PRODUCT_TYPE_CODE     STRING, 
     PIN_NUMBER            INT, 
     NATIONALITY           STRING, 
     PRIMARY_IDEN_DOC      STRING, 
     PRIMARY_IDEN_DOC_ID   STRING, 
     SECONDARY_IDEN_DOC    STRING, 
     SECONDARY_IDEN_DOC_ID STRING, 
     ACCOUNT_OPEN_DATE     STRING, 
     ACCOUNT_NUMBER        STRING, 
     OPENING_BALANCE       INT, 
     CURRENT_BALANCE       INT, 
     OVERDUE_BALANCE       INT, 
     OVERDUE_DATE          STRING, 
     CURRENCY_CODE         STRING, 
     INTEREST_TYPE         STRING, 
     INTEREST_RATE         DECIMAL 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DIM_ADDRESS; 

CREATE TABLE DIM_ADDRESS 
  ( 
     ADDRESS_ID     STRING, 
     SRC_ADDRESS_ID STRING, 
     ADDRESS_LINE1  STRING, 
     ADDRESS_LINE2  STRING, 
     ADDRESS_LINE3  STRING, 
     CITY           STRING, 
     COUNTY         STRING, 
     STATE          STRING, 
     ZIPCODE        INT, 
     COUNTRY        STRING, 
     LATITUDE       STRING, 
     LONGTITUDE     STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DIM_BANK; 

CREATE TABLE DIM_BANK 
  ( 
     BANK_ID             STRING, 
     SRC_BANK_ID         STRING, 
     BANK_CODE           STRING, 
     BANK_NAME           STRING, 
     BANK_ACCOUNT_NUMBER STRING, 
     BANK_CURRENCY_CODE  STRING, 
     BANK_CHECK_DIGITS   INT 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS DIM_BRANCH; 

CREATE TABLE DIM_BRANCH 
  ( 
     BRANCH_ID            STRING, 
     SRC_BRANCH_ID        STRING, 
     BRANCH_TYPE_CODE     STRING, 
     BRANCH_NAME          STRING, 
     BRANCH_DESC          STRING, 
     BRANCH_CONTACT_NAME  STRING, 
     BRANCH_CONTACT_PHONE STRING, 
     BRANCH_CONTACT_EMAIL STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DIM_COUNTRY; 

CREATE TABLE DIM_COUNTRY 
  ( 
     COUNTRY_ID         STRING, 
     COUNTRY_CODE       STRING, 
     COUNTRY_NAME       STRING, 
     COUNTRY_POPULATION INT 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DIM_CUSTOMER; 

CREATE TABLE DIM_CUSTOMER 
  ( 
     CUSTOMER_ID         STRING, 
     SRC_CUSTOMER_ID     STRING, 
     TITLE               STRING, 
     FIRST_NAME          STRING, 
     MIDDLE_NAME         STRING, 
     LAST_NAME           STRING, 
     ADDRESS_LINE1       STRING, 
     ADDRESS_LINE2       STRING, 
     PHONE               STRING, 
     DATE_FIRST_PURCHASE STRING, 
     COMMUTE_DISTANCE    INT, 
     CITY                STRING, 
     STATE               STRING, 
     POSTAL_CODE         STRING, 
     COUNTRY             STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS DIM_DATE; 

CREATE TABLE DIM_DATE 
  ( 
     DATE_ID                        STRING, 
     DATE_TYPE                      STRING, 
     DATE_VAL                       STRING, 
     DAY_NUM_OF_WEEK                INT, 
     DAY_NUM_OF_MONTH               INT, 
     DAY_NUM_OF_QUARTER             INT, 
     DAY_NUM_OF_YEAR                INT, 
     DAY_NUM_ABSOLUTE               INT, 
     DAY_OF_WEEK_NAME               STRING, 
     DAY_OF_WEEK_ABBREVIATION       STRING, 
     JULIAN_DAY_NUM_OF_YEAR         INT, 
     JULIAN_DAY_NUM_ABSOLUTE        INT, 
     IS_WEEKDAY                     STRING, 
     IS_USA_CIVIL_HOLIDAY           STRING, 
     IS_LAST_DAY_OF_WEEK            STRING, 
     IS_LAST_DAY_OF_MONTH           STRING, 
     IS_LAST_DAY_OF_QUARTER         STRING, 
     IS_LAST_DAY_OF_YEAR            STRING, 
     IS_LAST_DAY_OF_FISCAL_MONTH    STRING, 
     IS_LAST_DAY_OF_FISCAL_QUARTER  STRING, 
     IS_LAST_DAY_OF_FISCAL_YEAR     STRING, 
     WEEK_OF_YEAR_BEGIN_DATE        STRING, 
     WEEK_OF_YEAR_BEGIN_DATE_KEY    INT, 
     WEEK_OF_YEAR_END_DATE          STRING, 
     WEEK_OF_YEAR_END_DATE_KEY      INT, 
     WEEK_OF_MONTH_BEGIN_DATE       STRING, 
     WEEK_OF_MONTH_BEGIN_DATE_KEY   INT, 
     WEEK_OF_MONTH_END_DATE         STRING, 
     WEEK_OF_MONTH_END_DATE_KEY     INT, 
     WEEK_OF_QUARTER_BEGIN_DATE     STRING, 
     WEEK_OF_QUARTER_BEGIN_DATE_KEY INT, 
     WEEK_OF_QUARTER_END_DATE       STRING, 
     WEEK_OF_QUARTER_END_DATE_KEY   INT, 
     WEEK_NUM_OF_MONTH              INT, 
     WEEK_NUM_OF_QUARTER            INT, 
     WEEK_NUM_OF_YEAR               INT, 
     MONTH_NUM_OF_YEAR              INT, 
     MONTH_NUM_OVERALL              STRING, 
     MONTH_NAME                     STRING, 
     MONTH_NAME_ABBREVIATION        STRING, 
     MONTH_BEGIN_DATE               STRING, 
     MONTH_BEGIN_DATE_KEY           INT, 
     MONTH_END_DATE                 STRING, 
     MONTH_END_DATE_KEY             INT, 
     QUARTER_NUM_OF_YEAR            INT, 
     QUARTER_NUM_OVERALL            INT, 
     QUARTER_BEGIN_DATE             STRING, 
     QUARTER_BEGIN_DATE_KEY         INT, 
     QUARTER_END_DATE               STRING, 
     QUARTER_END_DATE_KEY           INT, 
     YEAR_NUM                       INT, 
     YEAR_BEGIN_DATE                STRING, 
     YEAR_BEGIN_DATE_KEY            INT, 
     YEAR_END_DATE                  STRING, 
     YEAR_END_DATE_KEY              INT, 
     YYYY_MM                        STRING, 
     YYYY_MM_DD                     STRING, 
     DD_MON_YYYY                    STRING 
  ) PARTITIONED BY (LOAD_DATE STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DIM_STATE; 

CREATE TABLE DIM_STATE 
  ( 
     STATE_ID         STRING, 
     STATE_CODE       STRING, 
     STATE_NAME       STRING, 
     COUNTRY_CODE     STRING, 
     STATE_POPULATION INT 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS DIM_TRANSACTION_TYPE; 

CREATE TABLE DIM_TRANSACTION_TYPE 
  ( 
     TRANSACTION_TYPE_ID     STRING, 
     SRC_TRANSACTION_TYPE_ID STRING, 
     TRANSACTION_TYPE_CODE   STRING, 
     TRANSACTION_TYPE_DESC   STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS DP_RULE_RESULTS; 

CREATE TABLE DP_RULE_RESULTS 
  ( 
     DATAPODUUID    STRING, 
     DATAPODVERSION STRING, 
     DATAPODNAME    STRING, 
     ATTRIBUTEID    STRING, 
     ATTRIBUTENAME  STRING, 
     NUMROWS        STRING, 
     MINVAL         DECIMAL, 
     MAXVAL         DECIMAL, 
     AVGVAL         DECIMAL, 
     MEDIANVAL      DECIMAL, 
     STDDEV         DECIMAL, 
     NUMDISTINCT    INT, 
     PERDISTINCT    DECIMAL, 
     NUMNULL        INT, 
     PERNULL        DECIMAL, 
     SIXSIGMA       DECIMAL, 
     VERSION        INT 
  );DROP TABLE IF EXISTS DQ_RULE_RESULTS; 

CREATE TABLE DQ_RULE_RESULTS 
  ( 
     ROWKEY                 STRING, 
     DATAPODUUID            STRING, 
     DATAPODVERSION         STRING, 
     DATAPODNAME            STRING, 
     ATTRIBUTEID            STRING, 
     ATTRIBUTENAME          STRING, 
     ATTRIBUTEVALUE         STRING, 
     NULLCHECK_PASS         STRING, 
     VALUECHECK_PASS        STRING, 
     RANGECHECK_PASS        STRING, 
     DATATYPECHECK_PASS     STRING, 
     DATAFORMATCHECK_PASS   STRING, 
     LENGTHCHECK_PASS       STRING, 
     REFINTEGRITYCHECK_PASS STRING, 
     DUPCHECK_PASS          STRING, 
     CUSTOMCHECK_PASS       STRING, 
     VERSION                INT 
  ); DROP TABLE IF EXISTS FACT_ACCOUNT_SUMMARY_MONTHLY; 

CREATE TABLE FACT_ACCOUNT_SUMMARY_MONTHLY 
  ( 
     ACCOUNT_ID             STRING, 
     YYYY_MM                STRING, 
     TOTAL_TRANS_COUNT      INT, 
     TOTAL_TRANS_AMOUNT_USD INT, 
     AVG_TRANS_AMOUNT       INT, 
     MIN_AMOUNT             DECIMAL, 
     MAX_AMOUNT             INT 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';DROP TABLE IF EXISTS FACT_CUSTOMER_SUMMARY_MONTHLY; 

CREATE TABLE FACT_CUSTOMER_SUMMARY_MONTHLY 
  ( 
     CUSTOMER_ID            STRING, 
     YYYY_MM                STRING, 
     TOTAL_TRANS_COUNT      STRING, 
     TOTAL_TRANS_AMOUNT_USD INT, 
     AVG_TRANS_AMOUNT       INT, 
     MIN_AMOUNT             DECIMAL, 
     MAX_AMOUNT             DECIMAL 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS FACT_TRANSACTION; 

CREATE TABLE FACT_TRANSACTION 
  ( 
     TRANSACTION_ID      STRING, 
     SRC_TRANSACTION_ID  STRING, 
     TRANSACTION_TYPE_ID INT, 
     TRANS_DATE_ID       INT, 
     BANK_ID             INT, 
     BRANCH_ID           INT, 
     CUSTOMER_ID         STRING, 
     ADDRESS_ID          STRING, 
     ACCOUNT_ID          STRING, 
     FROM_ACCOUNT        STRING, 
     TO_ACCOUNT          STRING, 
     AMOUNT_BASE_CURR    INT, 
     AMOUNT_USD          INT, 
     CURRENCY_CODE       STRING, 
     CURRENCY_RATE       INT, 
     NOTES               STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS MODEL_TRAINING_SET;

CREATE TABLE IF NOT EXISTS MODEL_TRAINING_SET
(
  CUSTOMER_ID STRING,
  ADDRESS_ID STRING,
  BRANCH_ID STRING,
  COMMUTE_DISTANCE_MILES INT,
  LABEL INT,
  CENSOR INT,
  VERSION STRING
);DROP TABLE IF EXISTS PRODUCT_TYPE; 

CREATE TABLE PRODUCT_TYPE 
  ( 
     PRODUCT_TYPE_ID   STRING, 
     PRODUCT_TYPE_CODE STRING, 
     PRODUCT_TYPE_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS RC_RULE_RESULTS; 

CREATE TABLE RC_RULE_RESULTS 
  ( 
     SOURCEUUID          STRING, 
     SOURCEVERSION       STRING, 
     SOURCENAME          STRING, 
     SOURCEATTRIBUTEID   STRING, 
     SOURCEATTRIBUTENAME STRING, 
     SOURCEVALUE         DECIMAL, 
     TARGETUUID          STRING, 
     TARGETVERSION       STRING, 
     TARGETNAME          STRING, 
     TARGETATTRIBUTEID   STRING, 
     TARGETATTRIBUTENAME STRING, 
     TARGETVALUE         DECIMAL, 
     STATUS              STRING, 
     VERSION             INT 
  ); DROP TABLE IF EXISTS TRANSACTION; 

CREATE TABLE TRANSACTION 
  ( 
     TRANSACTION_ID      STRING, 
     TRANSACTION_TYPE_ID STRING, 
     ACCOUNT_ID          STRING, 
     TRANSACTION_DATE    STRING, 
     FROM_ACCOUNT        STRING, 
     TO_ACCOUNT          STRING, 
     AMOUNT_BASE_CURR    DECIMAL, 
     AMOUNT_USD          DECIMAL, 
     CURRENCY_CODE       STRING, 
     CURRENCY_RATE       DECIMAL, 
     NOTES               STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; DROP TABLE IF EXISTS TRANSACTION_TYPE; 

CREATE TABLE TRANSACTION_TYPE 
  ( 
     TRANSACTION_TYPE_ID   STRING, 
     TRANSACTION_TYPE_CODE STRING, 
     TRANSACTION_TYPE_DESC STRING 
  ) PARTITIONED BY (LOAD_DATE STRING, LOAD_ID STRING) ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','; 