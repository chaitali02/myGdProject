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