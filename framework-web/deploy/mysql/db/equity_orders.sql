DROP TABLE IF EXISTS equity_orders; 

CREATE TABLE equity_orders 
  ( 
     order_id             INT(10), 
     version_id           INT(10), 
     order_event_type     VARCHAR(50), 
     order_placement_date VARCHAR(50), 
     order_placement_time VARCHAR(50), 
     order_event_date     VARCHAR(50), 
     order_event_time     VARCHAR(50), 
     time_in_force        VARCHAR(50), 
     order_type           VARCHAR(50), 
     limit_price          DOUBLE, 
     stop_price           DOUBLE, 
     trading_account_id   INT(10), 
     sales_trader_id      INT(10), 
     position_trader_id   INT(10), 
     security_id          INT(10), 
     security_symbol      VARCHAR(50), 
     ric_code             VARCHAR(50), 
     security_description VARCHAR(100), 
     client_account_id    INT(10), 
     parent_order_id      INT(10), 
     route_destination    VARCHAR(50), 
     quantity             DOUBLE, 
     reason               VARCHAR(500), 
     version              INT(10) 
  ); 