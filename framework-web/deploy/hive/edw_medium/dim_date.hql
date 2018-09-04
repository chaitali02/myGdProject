CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS dim_date;
CREATE TABLE IF NOT EXISTS `dim_date`(
  `date_id` int, 
  `date_type` string,
  `date_val` string,
  `day_num_of_week` int, 
  `day_num_of_month` int, 
  `day_num_of_quarter` int, 
  `day_num_of_year` int, 
  `day_num_absolute` int, 
  `day_of_week_name` string, 
  `day_of_week_abbreviation` string, 
  `julian_day_num_of_year` int, 
  `julian_day_num_absolute` int, 
  `is_weekday` string, 
  `is_usa_civil_holiday` string, 
  `is_last_day_of_week` string, 
  `is_last_day_of_month` string, 
  `is_last_day_of_quarter` string, 
  `is_last_day_of_year` string, 
  `is_last_day_of_fiscal_month` string, 
  `is_last_day_of_fiscal_quarter` string, 
  `is_last_day_of_fiscal_year` string, 
  `week_of_year_begin_date` string, 
  `week_of_year_begin_date_key` int, 
  `week_of_year_end_date` string, 
  `week_of_year_end_date_key` int, 
  `week_of_month_begin_date` string, 
  `week_of_month_begin_date_key` int, 
  `week_of_month_end_date` string, 
  `week_of_month_end_date_key` int, 
  `week_of_quarter_begin_date` string, 
  `week_of_quarter_begin_date_key` int, 
  `week_of_quarter_end_date` string, 
  `week_of_quarter_end_date_key` int, 
  `week_num_of_month` int, 
  `week_num_of_quarter` int, 
  `week_num_of_year` int, 
  `month_num_of_year` int, 
  `month_num_overall` string,
  `month_name` string, 
  `month_name_abbreviation` string, 
  `month_begin_date` string, 
  `month_begin_date_key` int, 
  `month_end_date` string, 
  `month_end_date_key` int, 
  `quarter_num_of_year` int, 
  `quarter_num_overall` int, 
  `quarter_begin_date` string, 
  `quarter_begin_date_key` int, 
  `quarter_end_date` string, 
  `quarter_end_date_key` int, 
  `year_num` int, 
  `year_begin_date` string, 
  `year_begin_date_key` int, 
  `year_end_date` string, 
  `year_end_date_key` int, 
  `yyyy_mm` string, 
  `yyyy_mm_dd` string,
  `dd_mon_yyyy` string)
   PARTITIONED BY ( 
  `load_date` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_medium.dim_date ADD PARTITION(load_date='2017-12-04');
