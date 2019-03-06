
drop table if exists device_data_compute_rolling_diff;
create table device_data_compute_rolling_diff
(deviceid  varchar(50), 
 date  varchar(50), 
 warn_type1_total_rollingdiff_3 decimal(10,3), 
 warn_type2_total_rollingdiff_3 decimal(10,3), 
 problem_type_1_rollingdiff_3 decimal(10,3), 
 problem_type_2_rollingdiff_3 decimal(10,3), 
 problem_type_3_rollingdiff_3 decimal(10,3), 
 problem_type_4_rollingdiff_3 decimal(10,3), 
 problem_type_1_per_usage1_rollingdiff_3 decimal(10,3), 
 problem_type_2_per_usage1_rollingdiff_3 decimal(10,3), 
 problem_type_3_per_usage1_rollingdiff_3 decimal(10,3), 
 problem_type_4_per_usage1_rollingdiff_3 decimal(10,3), 
 problem_type_1_per_usage2_rollingdiff_3 decimal(10,3), 
 problem_type_2_per_usage2_rollingdiff_3 decimal(10,3), 
 problem_type_3_per_usage2_rollingdiff_3 decimal(10,3), 
 problem_type_4_per_usage2_rollingdiff_3 decimal(10,3), 
 fault_code_type_1_count_rollingdiff_3 decimal(10,3), 
 fault_code_type_2_count_rollingdiff_3 decimal(10,3), 
 fault_code_type_3_count_rollingdiff_3 decimal(10,3), 
 fault_code_type_4_count_rollingdiff_3 decimal(10,3), 
 fault_code_type_1_count_per_usage1_rollingdiff_3 decimal(10,3), 
 fault_code_type_2_count_per_usage1_rollingdiff_3 decimal(10,3), 
 fault_code_type_3_count_per_usage1_rollingdiff_3 decimal(10,3), 
 fault_code_type_4_count_per_usage1_rollingdiff_3 decimal(10,3), 
 fault_code_type_1_count_per_usage2_rollingdiff_3 decimal(10,3), 
 fault_code_type_2_count_per_usage2_rollingdiff_3 decimal(10,3), 
 fault_code_type_3_count_per_usage2_rollingdiff_3 decimal(10,3), 
 fault_code_type_4_count_per_usage2_rollingdiff_3 decimal(10,3), 
 warn_type1_total_rollingdiff_7 decimal(10,3), 
 warn_type2_total_rollingdiff_7 decimal(10,3), 
 problem_type_1_rollingdiff_7 decimal(10,3), 
 problem_type_2_rollingdiff_7 decimal(10,3), 
 problem_type_3_rollingdiff_7 decimal(10,3), 
 problem_type_4_rollingdiff_7 decimal(10,3), 
 problem_type_1_per_usage1_rollingdiff_7 decimal(10,3), 
 problem_type_2_per_usage1_rollingdiff_7 decimal(10,3), 
 problem_type_3_per_usage1_rollingdiff_7 decimal(10,3), 
 problem_type_4_per_usage1_rollingdiff_7 decimal(10,3), 
 problem_type_1_per_usage2_rollingdiff_7 decimal(10,3), 
 problem_type_2_per_usage2_rollingdiff_7 decimal(10,3), 
 problem_type_3_per_usage2_rollingdiff_7 decimal(10,3), 
 problem_type_4_per_usage2_rollingdiff_7 decimal(10,3), 
 fault_code_type_1_count_rollingdiff_7 decimal(10,3), 
 fault_code_type_2_count_rollingdiff_7 decimal(10,3), 
 fault_code_type_3_count_rollingdiff_7 decimal(10,3), 
 fault_code_type_4_count_rollingdiff_7 decimal(10,3), 
 fault_code_type_1_count_per_usage1_rollingdiff_7 decimal(10,3), 
 fault_code_type_2_count_per_usage1_rollingdiff_7 decimal(10,3), 
 fault_code_type_3_count_per_usage1_rollingdiff_7 decimal(10,3), 
 fault_code_type_4_count_per_usage1_rollingdiff_7 decimal(10,3), 
 fault_code_type_1_count_per_usage2_rollingdiff_7 decimal(10,3), 
 fault_code_type_2_count_per_usage2_rollingdiff_7 decimal(10,3), 
 fault_code_type_3_count_per_usage2_rollingdiff_7 decimal(10,3), 
 fault_code_type_4_count_per_usage2_rollingdiff_7 decimal(10,3), 
 warn_type1_total_rollingdiff_14 decimal(10,3), 
 warn_type2_total_rollingdiff_14 decimal(10,3), 
 problem_type_1_rollingdiff_14 decimal(10,3), 
 problem_type_2_rollingdiff_14 decimal(10,3), 
 problem_type_3_rollingdiff_14 decimal(10,3), 
 problem_type_4_rollingdiff_14 decimal(10,3), 
 problem_type_1_per_usage1_rollingdiff_14 decimal(10,3), 
 problem_type_2_per_usage1_rollingdiff_14 decimal(10,3), 
 problem_type_3_per_usage1_rollingdiff_14 decimal(10,3), 
 problem_type_4_per_usage1_rollingdiff_14 decimal(10,3), 
 problem_type_1_per_usage2_rollingdiff_14 decimal(10,3), 
 problem_type_2_per_usage2_rollingdiff_14 decimal(10,3), 
 problem_type_3_per_usage2_rollingdiff_14 decimal(10,3), 
 problem_type_4_per_usage2_rollingdiff_14 decimal(10,3), 
 fault_code_type_1_count_rollingdiff_14 decimal(10,3), 
 fault_code_type_2_count_rollingdiff_14 decimal(10,3), 
 fault_code_type_3_count_rollingdiff_14 decimal(10,3), 
 fault_code_type_4_count_rollingdiff_14 decimal(10,3), 
 fault_code_type_1_count_per_usage1_rollingdiff_14 decimal(10,3), 
 fault_code_type_2_count_per_usage1_rollingdiff_14 decimal(10,3), 
 fault_code_type_3_count_per_usage1_rollingdiff_14 decimal(10,3), 
 fault_code_type_4_count_per_usage1_rollingdiff_14 decimal(10,3), 
 fault_code_type_1_count_per_usage2_rollingdiff_14 decimal(10,3), 
 fault_code_type_2_count_per_usage2_rollingdiff_14 decimal(10,3), 
 fault_code_type_3_count_per_usage2_rollingdiff_14 decimal(10,3), 
 fault_code_type_4_count_per_usage2_rollingdiff_14 decimal(10,3), 
 warn_type1_total_rollingdiff_30 decimal(10,3), 
 warn_type2_total_rollingdiff_30 decimal(10,3), 
 problem_type_1_rollingdiff_30 decimal(10,3), 
 problem_type_2_rollingdiff_30 decimal(10,3), 
 problem_type_3_rollingdiff_30 decimal(10,3), 
 problem_type_4_rollingdiff_30 decimal(10,3), 
 problem_type_1_per_usage1_rollingdiff_30 decimal(10,3), 
 problem_type_2_per_usage1_rollingdiff_30 decimal(10,3), 
 problem_type_3_per_usage1_rollingdiff_30 decimal(10,3), 
 problem_type_4_per_usage1_rollingdiff_30 decimal(10,3), 
 problem_type_1_per_usage2_rollingdiff_30 decimal(10,3), 
 problem_type_2_per_usage2_rollingdiff_30 decimal(10,3), 
 problem_type_3_per_usage2_rollingdiff_30 decimal(10,3), 
 problem_type_4_per_usage2_rollingdiff_30 decimal(10,3), 
 fault_code_type_1_count_rollingdiff_30 decimal(10,3), 
 fault_code_type_2_count_rollingdiff_30 decimal(10,3), 
 fault_code_type_3_count_rollingdiff_30 decimal(10,3), 
 fault_code_type_4_count_rollingdiff_30 decimal(10,3), 
 fault_code_type_1_count_per_usage1_rollingdiff_30 decimal(10,3), 
 fault_code_type_2_count_per_usage1_rollingdiff_30 decimal(10,3), 
 fault_code_type_3_count_per_usage1_rollingdiff_30 decimal(10,3), 
 fault_code_type_4_count_per_usage1_rollingdiff_30 decimal(10,3), 
 fault_code_type_1_count_per_usage2_rollingdiff_30 decimal(10,3), 
 fault_code_type_2_count_per_usage2_rollingdiff_30 decimal(10,3), 
 fault_code_type_3_count_per_usage2_rollingdiff_30 decimal(10,3), 
 fault_code_type_4_count_per_usage2_rollingdiff_30 decimal(10,3), 
 warn_type1_total_rollingdiff_90 decimal(10,3), 
 warn_type2_total_rollingdiff_90 decimal(10,3), 
 problem_type_1_rollingdiff_90 decimal(10,3), 
 problem_type_2_rollingdiff_90 decimal(10,3), 
 problem_type_3_rollingdiff_90 decimal(10,3), 
 problem_type_4_rollingdiff_90 decimal(10,3), 
 problem_type_1_per_usage1_rollingdiff_90 decimal(10,3), 
 problem_type_2_per_usage1_rollingdiff_90 decimal(10,3), 
 problem_type_3_per_usage1_rollingdiff_90 decimal(10,3), 
 problem_type_4_per_usage1_rollingdiff_90 decimal(10,3), 
 problem_type_1_per_usage2_rollingdiff_90 decimal(10,3), 
 problem_type_2_per_usage2_rollingdiff_90 decimal(10,3), 
 problem_type_3_per_usage2_rollingdiff_90 decimal(10,3), 
 problem_type_4_per_usage2_rollingdiff_90 decimal(10,3), 
 fault_code_type_1_count_rollingdiff_90 decimal(10,3), 
 fault_code_type_2_count_rollingdiff_90 decimal(10,3), 
 fault_code_type_3_count_rollingdiff_90 decimal(10,3), 
 fault_code_type_4_count_rollingdiff_90 decimal(10,3), 
 fault_code_type_1_count_per_usage1_rollingdiff_90 decimal(10,3), 
 fault_code_type_2_count_per_usage1_rollingdiff_90 decimal(10,3), 
 fault_code_type_3_count_per_usage1_rollingdiff_90 decimal(10,3), 
 fault_code_type_4_count_per_usage1_rollingdiff_90 decimal(10,3), 
 fault_code_type_1_count_per_usage2_rollingdiff_90 decimal(10,3), 
 fault_code_type_2_count_per_usage2_rollingdiff_90 decimal(10,3), 
 fault_code_type_3_count_per_usage2_rollingdiff_90 decimal(10,3), 
 fault_code_type_4_count_per_usage2_rollingdiff_90 decimal(10,3), 
 date_month   integer(50), 
 date_week   integer(50), 
 date_day   integer(50), 
 version   integer(50));
