DROP TABLE IF EXISTS equity_executions; 

CREATE TABLE equity_executions 
  ( 
     execution_id         INT(10), 
     version_id           INT(10), 
     execution_event_type VARCHAR(50), 
     order_id             INT(10), 
     execution_date       VARCHAR(50), 
     execution_time       VARCHAR(50), 
     execution_event_date VARCHAR(50), 
     execution_event_time VARCHAR(50), 
     execution_price      DOUBLE, 
     trading_account_id   INT(10), 
     position_trader_id   INT(10), 
     security_id          INT(10), 
     security_symbol      VARCHAR(50), 
     ric_code             VARCHAR(50), 
     security_description VARCHAR(100), 
     client_account_id    INT(10), 
     parent_order_id      INT(10), 
     quantity             DOUBLE, 
     reason               VARCHAR(500), 
     version              INT(10) 
  ); 