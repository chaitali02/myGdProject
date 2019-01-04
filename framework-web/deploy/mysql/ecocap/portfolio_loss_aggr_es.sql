drop table if exists portfolio_loss_aggr_es; 

create table portfolio_loss_aggr_es 
  ( 
     expected_loss    decimal(10, 2), 
     value_at_risk    decimal(10, 2), 
     economic_capital decimal(10, 2), 
     expected_sum     decimal(10, 2), 
     reporting_date   varchar(50), 
     version          integer(50) 
  );