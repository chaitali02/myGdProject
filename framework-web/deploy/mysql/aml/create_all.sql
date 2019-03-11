
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
  rule_name varchar(50),
  entity_type varchar(50),
  entity_id varchar(50),
  criteria_id varchar(50),
  criteria_name varchar(50),
  criteria_met_ind varchar(50),
  criteria_expr varchar(50),
  criteria_score  decimal(12,1),
  rule_version varchar(50),
  version varchar(50)
);

drop table if exists rule_result_summary;
create table rule_result_summary
(
 rule_uuid varchar(50),
  rule_name varchar(50),
  entity_type varchar(50),
  entity_id varchar(50),
    score decimal(22,1),
  rule_version varchar(50),
  version varchar(50)
);
