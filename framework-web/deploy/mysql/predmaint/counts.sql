  select tabname,cnt from
(
  select ' device_data_categorical_features ' as tabname, count(*) as cnt from  predmaint.device_data_categorical_features
 union select ' device_data_compute_rolling_diff ' as tabname, count(*) as cnt from  predmaint.device_data_compute_rolling_diff
 union select ' device_data_compute_rolling_max ' as tabname, count(*) as cnt from predmaint.device_data_compute_rolling_max
 union select ' device_data_compute_rolling_mean ' as tabname, count(*) as cnt from predmaint.device_data_compute_rolling_mean
 union select ' device_data_compute_rolling_min ' as tabname, count(*) as cnt from predmaint.device_data_compute_rolling_min
 union select ' device_data_compute_rolling_std ' as tabname, count(*) as cnt from  predmaint.device_data_compute_rolling_std
 union select ' device_data_dedup ' as tabname, count(*) as cnt from  predmaint.device_data_dedup
 union select ' device_data_fillna ' as tabname, count(*) as cnt from predmaint.device_data_fillna
 union select ' device_data_final_features ' as tabname, count(*) as cnt from predmaint.device_data_final_features 
 union select ' device_data_pca_summary_features ' as tabname, count(*) as cnt from predmaint.device_data_pca_summary_features 
 union select ' device_data_pca_warning_features ' as tabname, count(*) as cnt from predmaint.device_data_pca_warning_features
 union select ' device_data ' as tabname, count(*) as cnt from predmaint.device_data
) t1
order by tabname;
