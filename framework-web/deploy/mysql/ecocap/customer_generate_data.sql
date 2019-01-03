drop table if exists customer_generate_data; 

create table customer_generate_data 
  ( 
     id      integer(50), 
     data    decimal(10, 2), 
     version integer(50) 
  ); 