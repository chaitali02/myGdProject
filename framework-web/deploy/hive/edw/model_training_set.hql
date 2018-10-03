DROP TABLE IF EXISTS model_training_set;

CREATE TABLE IF NOT EXISTS model_training_set
(
  customer_id int,
  address_id int,
  branch_id int,
  commute_distance_miles int,
  label int,
  censor int,
  version int
);

