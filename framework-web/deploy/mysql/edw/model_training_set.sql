drop table if exists model_training_set;
create table model_training_set
(
  customer_id int(11) default null,
  address_id int(11) default null,
  branch_id int(11) default null,
  commute_distance_miles int(11) default null,
  label int(11) default null,
  censor int(11) default null,
  version int(11) default null
);