drop table if exists customer_alert_summary;

create table customer_alert_summary
(
  customer_id varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);

