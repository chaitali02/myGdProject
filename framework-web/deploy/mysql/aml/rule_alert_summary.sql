DROP TABLE IF EXISTS rule_alert_summary;

CREATE TABLE rule_alert_summary
( 
  rule_type VARCHAR(50),
  focus_type VARCHAR(50),
  alerted INTEGER(50), 
  non_alerted INTEGER(50), 
  business_date VARCHAR(50)
);

