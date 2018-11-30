drop table if exists industry_factor_mean; 

create table industry_factor_mean 
  ( 
     id             varchar(50), 
     mean           decimal(10, 2), 
     reporting_date varchar(50), 
     version        integer(50) 
  ); 