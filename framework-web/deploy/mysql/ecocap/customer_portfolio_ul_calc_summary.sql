drop table if exists customer_portfolio_ul_calc_summary; 

create table customer_portfolio_ul_calc_summary 
  ( 
     cust_id                varchar(50), 
     portfolio_ul_cust_sum  decimal(10, 2), 
     portfolio_ul_total_sum decimal(10, 2), 
     reporting_date         varchar(50), 
     version                integer(50) 
  ); 