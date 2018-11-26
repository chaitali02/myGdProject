drop table if exists rule_alert_summary;

create table rule_alert_summary
( 
  rule_type varchar(50),
  focus_type varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);


