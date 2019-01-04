drop table if exists predict_suspicious_activity_lr;

create table predict_suspicious_activity_lr (
  features varchar(10000), 
  rawprediction varchar(50), 
  probability varchar(50), 
  prediction decimal(10,3)
);
