drop table if exists customer_var_contribution_topn_perc; 

create table customer_var_contribution_topn_perc 
  ( 
     reporting_date        varchar(50), 
     top_n                 varchar(50), 
     var_contribution_perc decimal(10, 2), 
     version               integer(50) 
  ); 