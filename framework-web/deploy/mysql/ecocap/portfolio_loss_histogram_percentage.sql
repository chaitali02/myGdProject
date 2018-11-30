drop table if exists portfolio_loss_histogram_percentage; 

create table portfolio_loss_histogram_percentage 
  ( 
     reporting_date varchar(50), 
     bucket         varchar(50), 
     frequency      integer(50), 
     version        integer(50) 
  ); 