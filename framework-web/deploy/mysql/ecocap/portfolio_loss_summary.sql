drop table if exists portfolio_loss_summary; 

create table portfolio_loss_summary 
  ( 
     portfolio_avg_pd           decimal(10, 2), 
     portfolio_avg_lgd          decimal(10, 2), 
     portfolio_total_ead        decimal(10, 2), 
     portfolio_expected_loss    decimal(10, 2), 
     portfolio_value_at_risk    decimal(10, 2), 
     portfolio_economic_capital decimal(10, 2), 
     portfolio_expected_sum     decimal(10, 2), 
     portfolio_es_percentage    decimal(10, 2), 
     portfolio_val_percentage   decimal(10, 2), 
     portfolio_el_percentage    decimal(10, 2), 
     portfolio_ec_percentage    decimal(10, 2), 
     reporting_date             varchar(50), 
     version                    integer(50) 
  ); 