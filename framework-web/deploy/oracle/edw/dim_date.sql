DROP TABLE DIM_DATE; 
CREATE TABLE DIM_DATE 
  ( 
     DATE_ID                        VARCHAR2(50) NOT NULL, 
     DATE_TYPE                      VARCHAR2(45), 
     DATE_VAL                       VARCHAR2(45), 
     DAY_NUM_OF_WEEK                INTEGER, 
     DAY_NUM_OF_MONTH               INTEGER, 
     DAY_NUM_OF_QUARTER             INTEGER, 
     DAY_NUM_OF_YEAR                INTEGER, 
     DAY_NUM_ABSOLUTE               INTEGER, 
     DAY_OF_WEEK_NAME               VARCHAR2(100), 
     DAY_OF_WEEK_ABBREVIATION       VARCHAR2(45), 
     JULIAN_DAY_NUM_OF_YEAR         INTEGER, 
     JULIAN_DAY_NUM_ABSOLUTE        INTEGER, 
     IS_WEEKDAY                     VARCHAR2(50), 
     IS_USA_CIVIL_HOLIDAY           VARCHAR2(50), 
     IS_LAST_DAY_OF_WEEK            VARCHAR2(50), 
     IS_LAST_DAY_OF_MONTH           VARCHAR2(50), 
     IS_LAST_DAY_OF_QUARTER         VARCHAR2(50), 
     IS_LAST_DAY_OF_YEAR            VARCHAR2(50), 
     IS_LAST_DAY_OF_FISCAL_MONTH    VARCHAR2(50), 
     IS_LAST_DAY_OF_FISCAL_QUARTER  VARCHAR2(50), 
     IS_LAST_DAY_OF_FISCAL_YEAR     VARCHAR2(50), 
     WEEK_OF_YEAR_BEGIN_DATE        VARCHAR2(50), 
     WEEK_OF_YEAR_BEGIN_DATE_KEY    INTEGER, 
     WEEK_OF_YEAR_END_DATE          VARCHAR2(50), 
     WEEK_OF_YEAR_END_DATE_KEY      INTEGER, 
     WEEK_OF_MONTH_BEGIN_DATE       VARCHAR2(50), 
     WEEK_OF_MONTH_BEGIN_DATE_KEY   INTEGER, 
     WEEK_OF_MONTH_END_DATE         VARCHAR2(50), 
     WEEK_OF_MONTH_END_DATE_KEY     INTEGER, 
     WEEK_OF_QUARTER_BEGIN_DATE     VARCHAR2(50), 
     WEEK_OF_QUARTER_BEGIN_DATE_KEY INTEGER, 
     WEEK_OF_QUARTER_END_DATE       VARCHAR2(50), 
     WEEK_OF_QUARTER_END_DATE_KEY   INTEGER, 
     WEEK_NUM_OF_MONTH              INTEGER, 
     WEEK_NUM_OF_QUARTER            INTEGER, 
     WEEK_NUM_OF_YEAR               INTEGER, 
     MONTH_NUM_OF_YEAR              INTEGER, 
     MONTH_NUM_OVERALL              VARCHAR2(50), 
     MONTH_NAME                     VARCHAR2(100), 
     MONTH_NAME_ABBREVIATION        VARCHAR2(100), 
     MONTH_BEGIN_DATE               VARCHAR2(50), 
     MONTH_BEGIN_DATE_KEY           INTEGER, 
     MONTH_END_DATE                 VARCHAR2(50), 
     MONTH_END_DATE_KEY             INTEGER, 
     QUARTER_NUM_OF_YEAR            INTEGER, 
     QUARTER_NUM_OVERALL            INTEGER, 
     QUARTER_BEGIN_DATE             VARCHAR2(50), 
     QUARTER_BEGIN_DATE_KEY         INTEGER, 
     QUARTER_END_DATE               VARCHAR2(50), 
     QUARTER_END_DATE_KEY           INTEGER, 
     YEAR_NUM                       INTEGER, 
     YEAR_BEGIN_DATE                VARCHAR2(50), 
     YEAR_BEGIN_DATE_KEY            INTEGER, 
     YEAR_END_DATE                  VARCHAR2(50), 
     YEAR_END_DATE_KEY              INTEGER, 
     YYYY_MM                        VARCHAR2(50), 
     YYYY_MM_DD                     VARCHAR2(50), 
     DD_MON_YYYY                    VARCHAR2(50), 
     LOAD_DATE                      VARCHAR2(50), 
     LOAD_ID                        INTEGER, 
     CONSTRAINT DIM_DATE_PK PRIMARY KEY (DATE_ID) 
  ); 
