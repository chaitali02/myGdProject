drop table if exists customer_es_allocation; 

create table customer_es_allocation 
  ( 
     cust_id         varchar(50), 
     es_contribution decimal(10, 2), 
     es_allocation   decimal(10, 2), 
     reporting_date  varchar(50), 
     version         integer(50) 
  ); 
