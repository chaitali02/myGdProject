DROP TABLE framework.dim_date;

CREATE TABLE framework.dim_date
(
  date_id integer NOT NULL,
  date_type text,
  date_val text,
  day_num_of_week integer,
  day_num_of_month integer,
  day_num_of_quarter integer,
  day_num_of_year integer,
  day_num_absolute integer,
  day_of_week_name text,
  day_of_week_abbreviation text,
  julian_day_num_of_year integer,
  julian_day_num_absolute integer,
  is_weekday text,
  is_usa_civil_holiday text,
  is_last_day_of_week text,
  is_last_day_of_month text,
  is_last_day_of_quarter text,
  is_last_day_of_year text,
  is_last_day_of_fiscal_month text,
  is_last_day_of_fiscal_quarter text,
  is_last_day_of_fiscal_year text,
  week_of_year_begin_date text,
  week_of_year_begin_date_key integer,
  week_of_year_end_date text,
  week_of_year_end_date_key integer,
  week_of_month_begin_date text,
  week_of_month_begin_date_key integer,
  week_of_month_end_date text,
  week_of_month_end_date_key integer,
  week_of_quarter_begin_date text,
  week_of_quarter_begin_date_key integer,
  week_of_quarter_end_date text,
  week_of_quarter_end_date_key integer,
  week_num_of_month integer,
  week_num_of_quarter integer,
  week_num_of_year integer,
  month_num_of_year integer,
  month_num_overall text,
  month_name text,
  month_name_abbreviation text,
  month_begin_date text,
  month_begin_date_key integer,
  month_end_date text,
  month_end_date_key integer,
  quarter_num_of_year integer,
  quarter_num_overall integer,
  quarter_begin_date text,
  quarter_begin_date_key integer,
  quarter_end_date text,
  quarter_end_date_key integer,
  year_num integer,
  year_begin_date text,
  year_begin_date_key integer,
  year_end_date text,
  year_end_date_key integer,
  yyyy_mm text,
  yyyy_mm_dd text,
  dd_mon_yyyy text,
  load_date text NOT NULL,
  CONSTRAINT dim_date_pkey PRIMARY KEY (date_id, load_date),
  CONSTRAINT date_val UNIQUE (date_val, load_date)
);

ALTER TABLE framework.dim_date
  OWNER TO inferyx;



Copy framework.dim_date(date_id,
  date_type,
  date_val,
  day_num_of_week,
  day_num_of_month,
  day_num_of_quarter,
  day_num_of_year,
  day_num_absolute,
  day_of_week_name,
  day_of_week_abbreviation,
  julian_day_num_of_year,
  julian_day_num_absolute,
  is_weekday,
  is_usa_civil_holiday,
  is_last_day_of_week,
  is_last_day_of_month,
  is_last_day_of_quarter,
  is_last_day_of_year,
  is_last_day_of_fiscal_month,
  is_last_day_of_fiscal_quarter,
  is_last_day_of_fiscal_year,
  week_of_year_begin_date,
  week_of_year_begin_date_key,
  week_of_year_end_date,
  week_of_year_end_date_key,
  week_of_month_begin_date,
  week_of_month_begin_date_key,
  week_of_month_end_date,
  week_of_month_end_date_key,
  week_of_quarter_begin_date,
  week_of_quarter_begin_date_key,
  week_of_quarter_end_date,
  week_of_quarter_end_date_key,
  week_num_of_month,
  week_num_of_quarter,
  week_num_of_year,
  month_num_of_year,
  month_num_overall,
  month_name,
  month_name_abbreviation,
  month_begin_date,
  month_begin_date_key,
  month_end_date,
  month_end_date_key,
  quarter_num_of_year,
  quarter_num_overall,
  quarter_begin_date,
  quarter_begin_date_key,
  quarter_end_date,
  quarter_end_date_key,
  year_num,
  year_begin_date,
  year_begin_date_key,
  year_end_date,
  year_end_date_key,
  yyyy_mm,
  yyyy_mm_dd,
  dd_mon_yyyy,
  load_date)FROM '/user/hive/warehouse/framework/upload/dim_date.csv' delimiter ',' csv  header;
