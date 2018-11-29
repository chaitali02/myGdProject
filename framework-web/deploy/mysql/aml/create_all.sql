
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
constraint transaction_id_pk primary key(transaction_id));


drop table if exists predict_suspicious_activity_gbt;

create table predict_suspicious_activity_gbt 
  ( 
     label                                  integer(50), 
     in_wire_transfers_trans_count          integer(50), 
     in_wire_transfers_trans_amount_usd     decimal(10, 3), 
     out_wire_transfers_trans_count         integer(50), 
     out_wire_transfers_trans_amount_usd    decimal(10, 3), 
     net_wire_transfers_trans_count         integer(50), 
     net_wire_transfers_trans_amount_usd    decimal(10, 3), 
     in_cash_transfers_trans_count          integer(50), 
     in_cash_transfers_trans_amount_usd     decimal(10, 3), 
     out_cash_transfers_trans_count         integer(50), 
     out_cash_transfers_trans_amount_usd    decimal(10, 3), 
     net_cash_transfers_trans_count         integer(50), 
     net_cash_transfers_trans_amount_usd    decimal(10, 3), 
     all_wire_transfers_round_amount_usd    decimal(10, 3), 
     all_cash_transfers_round_amount_usd    decimal(10, 3), 
     all_cash_transfers_round_amount_count  integer(50), 
     all_wire_transfers_round_amount_count  integer(50), 
     all_cash_transfers_trans_count         integer(50), 
     all_wire_transfers_trans_count         integer(50), 
     in_cash_transfers_trans_amount_min     decimal(10, 3), 
     out_wire_transfers_trans_amount_min    decimal(10, 3), 
     all_wire_transfers_round_amount_min    decimal(10, 3), 
     all_cash_transfers_round_amount_min    decimal(10, 3), 
     in_wire_transfers_trans_amount_min     decimal(10, 3), 
     out_cash_transfers_trans_amount_min    decimal(10, 3), 
     all_cash_transfers_round_amount_max    decimal(10, 3), 
     all_wire_transfers_round_amount_max    decimal(10, 3), 
     in_wire_transfers_trans_amount_max     decimal(10, 3), 
     in_cash_transfers_trans_amount_max     decimal(10, 3), 
     out_wire_transfers_trans_amount_max    decimal(10, 3), 
     out_cash_transfers_trans_amount_max    decimal(10, 3), 
     all_wire_transfers_round_amount_avg    decimal(10, 3), 
     all_cash_transfers_round_amount_avg    decimal(10, 3), 
     in_wire_transfers_trans_amount_avg     decimal(10, 3), 
     in_cash_transfers_trans_amount_avg     decimal(10, 3), 
     out_wire_transfers_trans_amount_avg    decimal(10, 3), 
     out_cash_transfers_trans_amount_avg    decimal(10, 3), 
     all_cash_transfers_round_amount_stddev decimal(10, 3), 
     in_cash_transfers_trans_amount_stddev  decimal(10, 3), 
     out_cash_transfers_trans_amount_stddev decimal(10, 3), 
     all_wire_transfers_round_amount_stddev decimal(10, 3), 
     in_wire_transfers_trans_amount_stddev  decimal(10, 3), 
     out_wire_transfers_trans_amount_stddev decimal(10, 3), 
     in_cash_transfers_round_amount_count   integer(50), 
     in_wire_transfers_round_amount_count   integer(50), 
     out_wire_transfers_round_amount_count  integer(50), 
     out_cash_transfers_round_amount_count  integer(50), 
     out_cash_transfers_round_amount_sum    decimal(10, 3), 
     out_wire_transfers_round_amount_sum    decimal(10, 3), 
     in_wire_transfers_round_amount_sum     decimal(10, 3), 
     in_cash_transfers_round_amount_sum     decimal(10, 3), 
     features                               varchar(50), 
     rawprediction                          varchar(50), 
     probability                            varchar(50), 
     prediction                             decimal(10, 3) 
  ); 

drop table if exists predict_suspicious_activity_lr;

create table predict_suspicious_activity_lr 
(
  label integer(50),
  features varchar(50), 
  rawprediction varchar(50), 
  probability varchar(50), 
  prediction decimal(10,3));

drop table if exists rule_alert_summary;

create table rule_alert_summary
( 
  rule_type varchar(50),
  focus_type varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);


