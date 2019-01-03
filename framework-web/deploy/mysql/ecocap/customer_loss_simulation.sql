drop table if exists customer_loss_simulation; 

create table customer_loss_simulation 
  ( 
     cust_id        varchar(50), 
     iterationid    integer(50), 
     customer_loss  decimal(10, 2), 
     reporting_date varchar(50), 
     version        integer(50) 
  ); 