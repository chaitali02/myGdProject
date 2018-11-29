drop table if exists customer_portfolio_ul_calc; 

create table customer_portfolio_ul_calc 
  ( 
     cust_id1          varchar(50), 
     industry1         varchar(50), 
     correlation1      decimal(10, 2), 
     unexpected_loss1  decimal(10, 2), 
     cust_id2          varchar(50), 
     industry2         varchar(50), 
     correlation2      decimal(10, 2), 
     unexpected_loss2  decimal(10, 2), 
     factor_value      decimal(10, 2), 
     portfolio_ul_calc decimal(10, 2), 
     reporting_date    varchar(50), 
     version           integer(50) 
  ); 