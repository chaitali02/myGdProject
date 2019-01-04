drop table if exists customer_portfolio_ul_calc_allocation; 

create table customer_portfolio_ul_calc_allocation 
  ( 
     cust_id                      varchar(50), 
     portfolio_ul_cust_allocation decimal(10, 2), 
     reporting_date               varchar(50), 
     version                      integer(50) 
  ); 