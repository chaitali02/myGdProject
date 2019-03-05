drop table if exists cc_transactions_predictions;
create table cc_transactions_predictions(	
label	decimal(10, 2),
features   varchar(50),
rawPrediction   varchar(50),
probability   varchar(50),
prediction decimal(10, 2));
