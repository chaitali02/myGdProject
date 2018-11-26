DROP TABLE IF EXISTS predict_suspicious_activity_lr;

CREATE TABLE predict_suspicious_activity_lr 
(
  label INTEGER(50),
  features VARCHAR(50), 
  rawPrediction VARCHAR(50), 
  probability VARCHAR(50), 
  prediction DECIMAL(10,3));

