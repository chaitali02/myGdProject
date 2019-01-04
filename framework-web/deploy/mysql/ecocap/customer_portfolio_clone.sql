drop table if exists customer_portfolio_clone; 

create table customer_portfolio_clone 
  ( 
     cust_id          varchar(50), 
     industry         varchar(50), 
     pd               decimal(10, 2), 
     exposure         integer(50), 
     lgd              decimal(10, 2), 
     lgd_var          integer(50), 
     correlation      decimal(10, 2), 
     sqrt_correlation decimal(10, 2), 
     def_point        decimal(10, 2), 
     reporting_date   varchar(50), 
     version          integer(50) 
  ); 