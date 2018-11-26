DROP TABLE IF EXISTS customer_alert_summary;

CREATE TABLE customer_alert_summary
(
  customer_id VARCHAR(50),
  alerted INTEGER(50), 
  non_alerted INTEGER(50), 
  business_date VARCHAR(50)
);

