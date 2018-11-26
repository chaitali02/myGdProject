DROP TABLE IF EXISTS country_alert_summary;

CREATE TABLE country_alert_summary
(
  country_code VARCHAR(50),
  alerted INTEGER(50), 
  non_alerted INTEGER(50), 
  business_date VARCHAR(50)
);

