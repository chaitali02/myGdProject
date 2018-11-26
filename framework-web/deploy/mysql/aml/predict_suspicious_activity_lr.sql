drop table if exists predict_suspicious_activity_lr;

create table predict_suspicious_activity_lr 
(
  label integer(50),
  features varchar(50), 
  rawprediction varchar(50), 
  probability varchar(50), 
  prediction decimal(10,3));
