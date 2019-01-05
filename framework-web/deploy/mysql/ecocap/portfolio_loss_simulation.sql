drop table if exists portfolio_loss_simulation; 

create table portfolio_loss_simulation 
  ( 
     iterationid    integer(50), 
     portfolio_loss decimal(10, 2), 
     reporting_date varchar(50), 
     version        integer(50) 
  ); 