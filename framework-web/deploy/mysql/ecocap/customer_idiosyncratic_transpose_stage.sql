drop table if exists customer_idiosyncratic_transpose_stage; 

create table customer_idiosyncratic_transpose_stage 
  ( 
     iterationid integer(50), 
     customer    varchar(50), 
     pd          decimal(10, 2), 
     version     integer(50) 
  ); 