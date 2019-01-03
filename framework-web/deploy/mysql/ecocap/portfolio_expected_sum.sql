drop table if exists portfolio_expected_sum; 

create table portfolio_expected_sum 
  ( 
     expected_sum   decimal(10, 2), 
     reporting_date varchar(50), 
     version        integer(50) 
  ); 
