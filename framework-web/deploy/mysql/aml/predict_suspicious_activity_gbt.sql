drop table if exists predict_suspicious_activity_gbt;

CREATE TABLE predict_suspicious_activity_gbt (
  label int(50) DEFAULT NULL,
  in_wire_transfers_trans_count int(50) DEFAULT NULL,
  in_wire_transfers_trans_amount_usd decimal(15,3) DEFAULT NULL,
  out_wire_transfers_trans_count int(50) DEFAULT NULL,
  out_wire_transfers_trans_amount_usd decimal(15,3) DEFAULT NULL,
  net_wire_transfers_trans_count int(50) DEFAULT NULL,
  net_wire_transfers_trans_amount_usd decimal(15,3) DEFAULT NULL,
  in_cash_transfers_trans_count int(50) DEFAULT NULL,
  in_cash_transfers_trans_amount_usd decimal(15,3) DEFAULT NULL,
  out_cash_transfers_trans_count int(50) DEFAULT NULL,
  out_cash_transfers_trans_amount_usd decimal(15,3) DEFAULT NULL,
  net_cash_transfers_trans_count int(50) DEFAULT NULL,
  net_cash_transfers_trans_amount_usd decimal(15,3) DEFAULT NULL,
  all_wire_transfers_round_amount_usd decimal(15,3) DEFAULT NULL,
  all_cash_transfers_round_amount_usd decimal(15,3) DEFAULT NULL,
  all_cash_transfers_round_amount_count int(50) DEFAULT NULL,
  all_wire_transfers_round_amount_count int(50) DEFAULT NULL,
  all_cash_transfers_trans_count int(50) DEFAULT NULL,
  all_wire_transfers_trans_count int(50) DEFAULT NULL,
  in_cash_transfers_trans_amount_min decimal(15,3) DEFAULT NULL,
  out_wire_transfers_trans_amount_min decimal(15,3) DEFAULT NULL,
  all_wire_transfers_round_amount_min decimal(15,3) DEFAULT NULL,
  all_cash_transfers_round_amount_min decimal(15,3) DEFAULT NULL,
  in_wire_transfers_trans_amount_min decimal(15,3) DEFAULT NULL,
  out_cash_transfers_trans_amount_min decimal(15,3) DEFAULT NULL,
  all_cash_transfers_round_amount_max decimal(15,3) DEFAULT NULL,
  all_wire_transfers_round_amount_max decimal(15,3) DEFAULT NULL,
  in_wire_transfers_trans_amount_max decimal(15,3) DEFAULT NULL,
  in_cash_transfers_trans_amount_max decimal(15,3) DEFAULT NULL,
  out_wire_transfers_trans_amount_max decimal(15,3) DEFAULT NULL,
  out_cash_transfers_trans_amount_max decimal(15,3) DEFAULT NULL,
  all_wire_transfers_round_amount_avg decimal(15,3) DEFAULT NULL,
  all_cash_transfers_round_amount_avg decimal(15,3) DEFAULT NULL,
  in_wire_transfers_trans_amount_avg decimal(15,3) DEFAULT NULL,
  in_cash_transfers_trans_amount_avg decimal(15,3) DEFAULT NULL,
  out_wire_transfers_trans_amount_avg decimal(15,3) DEFAULT NULL,
  out_cash_transfers_trans_amount_avg decimal(15,3) DEFAULT NULL,
  all_cash_transfers_round_amount_stddev decimal(15,3) DEFAULT NULL,
  in_cash_transfers_trans_amount_stddev decimal(15,3) DEFAULT NULL,
  out_cash_transfers_trans_amount_stddev decimal(15,3) DEFAULT NULL,
  all_wire_transfers_round_amount_stddev decimal(15,3) DEFAULT NULL,
  in_wire_transfers_trans_amount_stddev decimal(15,3) DEFAULT NULL,
  out_wire_transfers_trans_amount_stddev decimal(15,3) DEFAULT NULL,
  in_cash_transfers_round_amount_count int(50) DEFAULT NULL,
  in_wire_transfers_round_amount_count int(50) DEFAULT NULL,
  out_wire_transfers_round_amount_count int(50) DEFAULT NULL,
  out_cash_transfers_round_amount_count int(50) DEFAULT NULL,
  out_cash_transfers_round_amount_sum decimal(15,3) DEFAULT NULL,
  out_wire_transfers_round_amount_sum decimal(15,3) DEFAULT NULL,
  in_wire_transfers_round_amount_sum decimal(15,3) DEFAULT NULL,
  in_cash_transfers_round_amount_sum decimal(15,3) DEFAULT NULL,
  features varchar(10000) DEFAULT NULL,
  rawprediction varchar(500) DEFAULT NULL,
  probability varchar(500) DEFAULT NULL,
  prediction decimal(10,3) DEFAULT NULL
);
