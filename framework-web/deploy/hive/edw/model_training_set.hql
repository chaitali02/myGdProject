DROP TABLE IF EXISTS model_training_set;

CREATE TABLE IF NOT EXISTS model_training_set
(
  customer_id string,
  address_id string,
  branch_id string,
  commute_distance_miles int,
  label int,
  censor int,
  version string
);

