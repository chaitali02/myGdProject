drop table if exists customer_idiosyncratic_transpose; 

create table customer_idiosyncratic_transpose 
  ( 
     iterationid    integer(50), 
     reporting_date varchar(50), 
     customer       varchar(50), 
     pd             decimal(10, 2), 
     version        integer(50) 
  ); 