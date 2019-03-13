drop table if exists rule_result_detail;
create table rule_result_detail
(
  rule_uuid varchar(50),
  rule_version int(10),
  rule_name varchar(100),
  entity_type varchar(50),
  entity_id varchar(50),
  criteria_id varchar(50),
  criteria_name varchar(100),
  criteria_met_ind varchar(50),
  criteria_expr varchar(500),
  criteria_score decimal(6,3),
  version int(10)
);