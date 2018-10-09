OPTIONS (DIRECT=TRUE, ERRORS=50, rows=1000000)
UNRECOVERABLE
LOAD DATA
INFILE '/user/hive/warehouse/framework/app/edw/data/csv/noheader/dim_date.csv'
TRUNCATE
INTO TABLE DIM_DATE
FIELDS TERMINATED BY ','
TRAILING NULLCOLS (DATE_ID,
		   DATE_TYPE,
		   DATE_VAL,
		   DAY_NUM_OF_WEEK,
		   DAY_NUM_OF_MONTH,
		   DAY_NUM_OF_QUARTER,
		   DAY_NUM_OF_YEAR,
		   DAY_NUM_ABSOLUTE,
		   DAY_OF_WEEK_NAME,
		   DAY_OF_WEEK_ABBREVIATION,
		   JULIAN_DAY_NUM_OF_YEAR,
		   JULIAN_DAY_NUM_ABSOLUTE,
		   IS_WEEKDAY,
		   IS_USA_CIVIL_HOLIDAY,
		   IS_LAST_DAY_OF_WEEK,
		   IS_LAST_DAY_OF_MONTH,
		   IS_LAST_DAY_OF_QUARTER,
		   IS_LAST_DAY_OF_YEAR,
		   IS_LAST_DAY_OF_FISCAL_MONTH,
		   IS_LAST_DAY_OF_FISCAL_QUARTER,
		   IS_LAST_DAY_OF_FISCAL_YEAR,
		   WEEK_OF_YEAR_BEGIN_DATE,
		   WEEK_OF_YEAR_BEGIN_DATE_KEY,
		   WEEK_OF_YEAR_END_DATE,
		   WEEK_OF_YEAR_END_DATE_KEY,
		   WEEK_OF_MONTH_BEGIN_DATE,
		   WEEK_OF_MONTH_BEGIN_DATE_KEY,
		   WEEK_OF_MONTH_END_DATE,
		   WEEK_OF_MONTH_END_DATE_KEY,
		   WEEK_OF_QUARTER_BEGIN_DATE,
		   WEEK_OF_QUARTER_BEGIN_DATE_KEY,
		   WEEK_OF_QUARTER_END_DATE,
		   WEEK_OF_QUARTER_END_DATE_KEY,
		   WEEK_NUM_OF_MONTH,
		   WEEK_NUM_OF_QUARTER,
		   WEEK_NUM_OF_YEAR,
		   MONTH_NUM_OF_YEAR,
		   MONTH_NUM_OVERALL,
		   MONTH_NAME,
		   MONTH_NAME_ABBREVIATION,
		   MONTH_BEGIN_DATE,
		   MONTH_BEGIN_DATE_KEY,
		   MONTH_END_DATE,
		   MONTH_END_DATE_KEY,
		   QUARTER_NUM_OF_YEAR,
		   QUARTER_NUM_OVERALL,
		   QUARTER_BEGIN_DATE,
		   QUARTER_BEGIN_DATE_KEY,
		   QUARTER_END_DATE,
		   QUARTER_END_DATE_KEY,
		   YEAR_NUM,
		   YEAR_BEGIN_DATE,
		   YEAR_BEGIN_DATE_KEY,
		   YEAR_END_DATE,
		   YEAR_END_DATE_KEY,
		   YYYY_MM,
		   YYYY_MM_DD,
		   DD_MON_YYYY 
   )
