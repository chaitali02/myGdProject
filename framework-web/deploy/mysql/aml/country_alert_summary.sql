drop table if exists country_alert_summary;

create table country_alert_summary
(
  country_code varchar(50),
  alerted integer(50), 
  non_alerted integer(50), 
  business_date varchar(50)
);

