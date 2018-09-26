DROP TABLE IF EXISTS model_training_set;

CREATE TABLE model_training_set
(
  customer_id int(11) DEFAULT NULL,
  address_id int(11) DEFAULT NULL,
  branch_id int(11) DEFAULT NULL,
  commute_distance_miles int(11) DEFAULT NULL,
  label int(11) DEFAULT NULL,
  censor int(11) DEFAULT NULL,
  version int(11) DEFAULT NULL
);

ALTER TABLE model_training_set OWNER TO inferyx;
