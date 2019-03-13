drop table if exists equity_executions;
 create table equity_executions(
execution_id int(10),
version_id int(10),
execution_event_type varchar(50),
order_id  int(10),
execution_date varchar(50),
execution_time varchar(50),
execution_event_date varchar(50),
execution_event_time varchar(50),
execution_price double,
trading_account_id int(10),
position_trader_id int(10),
security_id int(10),
security_symbol varchar(50),
ric_code  varchar(50),
security_description varchar(50),
client_account_id int(10),
parent_order_id int(10),
quantity double,
reason varchar(50),
version  int(10));

