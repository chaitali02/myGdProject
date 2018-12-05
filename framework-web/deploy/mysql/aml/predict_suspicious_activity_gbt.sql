drop table if exists predict_suspicious_activity_gbt;

CREATE TABLE predict_suspicious_activity_gbt (
  features varchar(10000) DEFAULT NULL,
  rawprediction varchar(500) DEFAULT NULL,
  probability varchar(500) DEFAULT NULL,
  prediction decimal(10,3) DEFAULT NULL
);
