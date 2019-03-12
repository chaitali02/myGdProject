
 drop table if exists account_summary_daily;
create table account_summary_daily
( 
	    customer_id varchar(50), 
	    account_id varchar(50) , 
	    trans_start_date varchar(50) , 
	    trans_end_date varchar(50), 
	    day_num varchar(50) , 
        in_wire_transfers_trans_count  integer(50) , 
        in_wire_transfers_trans_amount_usd  decimal(10,0) , 
        out_wire_transfers_trans_count  integer(50) , 
        out_wire_transfers_trans_amount_usd  decimal(10,0) , 
        net_wire_transfers_trans_count  integer(50) , 
        net_wire_transfers_trans_amount_usd  decimal(10,0) , 
        in_cash_transfers_trans_count  integer(50) , 
        in_cash_transfers_trans_amount_usd  decimal(10,0) , 
        out_cash_transfers_trans_count  integer(50) , 
        out_cash_transfers_trans_amount_usd  decimal(10,0) , 
        net_cash_transfers_trans_count  integer(50) , 
        net_cash_transfers_trans_amount_usd  decimal(10,0) , 
        all_wire_transfers_round_amount_usd  decimal(10,0) , 
        all_cash_transfers_round_amount_usd  decimal(10,0) , 
        all_cash_transfers_round_amount_count  integer(50) , 
        all_wire_transfers_round_amount_count  integer(50) , 
        all_cash_transfers_trans_count  integer(50) , 
        all_wire_transfers_trans_count  integer(50) , 
        in_cash_transfers_trans_amount_min  decimal(10,0) , 
        out_wire_transfers_trans_amount_min  decimal(10,0) , 
        all_wire_transfers_round_amount_min  decimal(10,0) , 
        all_cash_transfers_round_amount_min  decimal(10,0) , 
        in_wire_transfers_trans_amount_min  decimal(10,0) , 
        out_cash_transfers_trans_amount_min  decimal(10,0) , 
        all_cash_transfers_round_amount_max  decimal(10,0) , 
        all_wire_transfers_round_amount_max  decimal(10,0) , 
        in_wire_transfers_trans_amount_max  decimal(10,0) , 
        in_cash_transfers_trans_amount_max  decimal(10,0) , 
        out_wire_transfers_trans_amount_max  decimal(10,0) , 
        out_cash_transfers_trans_amount_max  decimal(10,0) , 
        all_wire_transfers_round_amount_avg  decimal(10,0) , 
        all_cash_transfers_round_amount_avg  decimal(10,0) , 
        in_wire_transfers_trans_amount_avg  decimal(10,0) , 
        in_cash_transfers_trans_amount_avg  decimal(10,0) , 
        out_wire_transfers_trans_amount_avg  decimal(10,0) , 
        out_cash_transfers_trans_amount_avg  decimal(10,0) , 
        all_cash_transfers_round_amount_stddev  decimal(10,0) , 
        in_cash_transfers_trans_amount_stddev  decimal(10,0) , 
        out_cash_transfers_trans_amount_stddev  decimal(10,0) , 
        all_wire_transfers_round_amount_stddev  decimal(10,0) , 
        in_wire_transfers_trans_amount_stddev  decimal(10,0) , 
        out_wire_transfers_trans_amount_stddev  decimal(10,0) , 
        in_cash_transfers_round_amount_count  integer(50) , 
        in_wire_transfers_round_amount_count  integer(50) , 
        out_wire_transfers_round_amount_count  integer(50) , 
        out_cash_transfers_round_amount_count  integer(50) , 
        out_cash_transfers_round_amount_sum  decimal(10,0) , 
        out_wire_transfers_round_amount_sum  decimal(10,0) , 
        in_wire_transfers_round_amount_sum  decimal(10,0) , 
        in_cash_transfers_round_amount_sum  decimal(10,0)   
  );

drop table if exists country_alert_summary;

create table country_alert_summary
(
  country_code varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);


drop table if exists customer_alert_summary;

create table customer_alert_summary
(
  customer_id varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);


drop table if exists customer_summary; 
create table customer_summary (
    customer_id varchar(100),
    in_wire_transfers_trans_count integer(10),
    in_wire_transfers_trans_amount_usd decimal(10,2),
    out_wire_transfers_trans_count integer(10),
    out_wire_transfers_trans_amount_usd decimal(10,2),
    net_wire_transfers_trans_count integer(10),
    net_wire_transfers_trans_amount_usd decimal(10,2),
    in_cash_transfers_trans_count integer(10),
    in_cash_transfers_trans_amount_usd decimal(10,2),
    out_cash_transfers_trans_count integer(10),
    out_cash_transfers_trans_amount_usd decimal(10,2),
    net_cash_transfers_trans_count integer(10),
    net_cash_transfers_trans_amount_usd decimal(10,2),
    all_wire_transfers_round_amount_usd decimal(10,2),
    all_cash_transfers_round_amount_usd decimal(10,2),
    all_cash_transfers_round_amount_count integer(10),
    all_wire_transfers_round_amount_count integer(10),
    all_cash_transfers_trans_count integer(10),
    all_wire_transfers_trans_count integer(10),
    in_cash_transfers_trans_amount_min decimal(10,2),
    out_wire_transfers_trans_amount_min decimal(10,2),
    all_wire_transfers_round_amount_min decimal(10,2),
    all_cash_transfers_round_amount_min decimal(10,2),
    in_wire_transfers_trans_amount_min decimal(10,2),
    out_cash_transfers_trans_amount_min decimal(10,2),
    all_cash_transfers_round_amount_max decimal(10,2),
    all_wire_transfers_round_amount_max decimal(10,2),
    in_wire_transfers_trans_amount_max decimal(10,2),
    in_cash_transfers_trans_amount_max decimal(10,2),
    out_wire_transfers_trans_amount_max decimal(10,2),
    out_cash_transfers_trans_amount_max decimal(10,2),
    all_wire_transfers_round_amount_avg decimal(10,2),
    all_cash_transfers_round_amount_avg decimal(10,2),
    in_wire_transfers_trans_amount_avg decimal(10,2),
    in_cash_transfers_trans_amount_avg decimal(10,2),
    out_wire_transfers_trans_amount_avg decimal(10,2),
    out_cash_transfers_trans_amount_avg decimal(10,2),
    all_cash_transfers_round_amount_stddev decimal(10,2),
    in_cash_transfers_trans_amount_stddev decimal(10,2),
    out_cash_transfers_trans_amount_stddev decimal(10,2),
    all_wire_transfers_round_amount_stddev decimal(10,2),
    in_wire_transfers_trans_amount_stddev decimal(10,2),
    out_wire_transfers_trans_amount_stddev decimal(10,2),
    in_cash_transfers_round_amount_count integer(10),
    in_wire_transfers_round_amount_count integer(10),
    out_wire_transfers_round_amount_count integer(10),
    out_cash_transfers_round_amount_count integer(10),
    out_cash_transfers_round_amount_sum decimal(10,2),
    out_wire_transfers_round_amount_sum decimal(10,2),
    in_wire_transfers_round_amount_sum decimal(10,2),
    in_cash_transfers_round_amount_sum decimal(10,2),
    suspicious_ind integer(1),
    constraint customer_summary_pk primary key(customer_id));
drop table if exists dim_country;
create table dim_country(	
	country_code varchar(10),
	country_name varchar(100),
	country_population integer(10), 
    country_risk_level integer(50),
	load_date varchar(10),
	load_id integer(50));

drop table if exists dq_rule_detail;
 create table dq_rule_detail (
rule_uuid varchar(50),
rule_version int(10) ,
rule_name varchar(50),
datapod_uuid varchar(50),
datapod_version int(10) ,
datapod_name varchar(50),
attribute_id varchar(50),
attribute_name varchar(50),
attribute_value varchar(500),
rowkey_name varchar(500),
rowkey_value varchar(500),
null_check_pass varchar(1) ,
value_check_pass varchar(1) ,
range_check_pass varchar(1) ,
datatype_check_pass varchar(1) ,
format_check_pass varchar(1) ,
length_check_pass varchar(1) ,
ri_check_pass varchar(1) ,
dup_check_pass varchar(1) ,
custom_check_pass varchar(1) ,
domain_check_pass varchar(1) ,
blankspace_check_pass varchar(1) ,
expression_check_pass varchar(1) ,
version int(10));


 

drop table if exists dq_rule_summary;
 create table dq_rule_summary (
    rule_uuid        varchar(50),
 rule_version     int(10),
 rule_name        varchar(50),
 datapod_uuid     varchar(50),
 datapod_version  int(10),
 datapod_name     varchar(50),
 all_check_pass   varchar(1),
 total_row_count  int(10),
 total_pass_count int(10),
 total_fail_count int(10),
 threshold_type   varchar(50),
 threshold_limit  int(3),
 threshold_ind    varchar(5),
 score            int(3),
 version          int(10));


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


drop table if exists equity_orders;
 create table equity_orders(
order_id int(10),
version_id int(10),
order_event_type varchar(50),
order_placement_date varchar(50),
order_placement_time varchar(50),
order_event_date varchar(50),
order_event_time varchar(50),
time_in_force varchar(50),
order_type varchar(50),
limit_price double,
stop_price double,
trading_account_id int(10),
sales_trader_id  int(10),
position_trader_id int(10),
security_id int(10),
security_symbol  varchar(50),
ric_code varchar(50),
security_description varchar(50),
client_account_id int(10),
parent_order_id  int(10),
route_destination varchar(50),
quantity double,
reason varchar(50),
version  int(10));


drop table if exists fact_transaction_journal;

create table fact_transaction_journal 
(
  transaction_id varchar(50),
  direction varchar(50), 
  account_id varchar(50), 
  customer_id varchar(50), 
  transaction_type_code varchar(50), 
  transaction_date varchar(50), 
  transaction_country varchar(50), 
  sender_country varchar(50), 
  reciever_country varchar(50), 
  amount_usd decimal(10,3), 
  load_date varchar(50), 
  load_id integer(50), 
check_num integer(50), 
constraint transaction_id_pk primary key(transaction_id,direction));


drop table if exists predict_suspicious_activity_gbt;

CREATE TABLE predict_suspicious_activity_gbt (
  features varchar(10000) DEFAULT NULL,
  rawprediction varchar(500) DEFAULT NULL,
  probability varchar(500) DEFAULT NULL,
  prediction decimal(10,3) DEFAULT NULL
);

drop table if exists predict_suspicious_activity_lr;

create table predict_suspicious_activity_lr (
  features varchar(10000), 
  rawprediction varchar(50), 
  probability varchar(50), 
  prediction decimal(10,3)
);

drop table if exists rule_alert_summary;

create table rule_alert_summary
( 
  rule_type varchar(50),
  focus_type varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);



drop table if exists rule_result_detail;
create table rule_result_detail
(
 rule_uuid varchar(50),
  rule_version varchar(50),
  rule_name varchar(50),
  entity_type varchar(50),
  entity_id varchar(50),
  criteria_id varchar(50),
  criteria_name varchar(50),
  criteria_met_ind varchar(50),
  criteria_expr varchar(50),
  criteria_score  decimal(12,1),
  version varchar(50)
);

drop table if exists rule_result_summary;
create table rule_result_summary
(
 rule_uuid varchar(50),
  rule_version varchar(50),
  rule_name varchar(50),
  entity_type varchar(50),
  entity_id varchar(50),
    score decimal(22,1),
  version varchar(50)
);

drop table if exists security;
create table security(	
	security_id int(10),
	security_symbol varchar(50),
	ric_code varchar(50),
	security_description varchar(50),
	version int(10));

	 
