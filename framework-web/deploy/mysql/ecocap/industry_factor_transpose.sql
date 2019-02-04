drop table if exists industry_factor_transpose; 

create table industry_factor_transpose 
  ( 
     iteration_id   integer(50), 
     reporting_date varchar(50), 
     factor         varchar(50), 
     factor_value   decimal(10, 2), 
     version        integer(50) 
  );