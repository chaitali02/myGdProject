drop table if exists industry_factor_simulation; 

create table industry_factor_simulation 
  ( 
     iteration_id   integer(50), 
     factor1        decimal(10, 2), 
     factor2        decimal(10, 2), 
     factor3        decimal(10, 2), 
     factor4        decimal(10, 2), 
     reporting_date varchar(50), 
     version        integer(50) 
  ); 