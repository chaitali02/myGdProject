drop table if exists industry_factor_correlation_transpose; 

create table industry_factor_correlation_transpose 
  ( 
     factor_x       varchar(50), 
     reporting_date varchar(50), 
     factor_y       varchar(50), 
     factor_value   decimal(10, 2), 
     version        integer(50) 
  ); 