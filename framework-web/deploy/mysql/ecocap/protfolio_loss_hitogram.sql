drop table if exists portfolio_loss_histogram; 

create table portfolio_loss_histogram 
  ( 
     reporting_date varchar(50), 
     bucket         varchar(50), 
     frequency      integer(50), 
     version        integer(50) 
  );