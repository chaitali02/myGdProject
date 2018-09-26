DROP TABLE IF EXISTS model_training_set;

CREATE TABLE model_training_set
(
  customer_id integer NOT NULL,
  address_id integer NOT NULL,
  branch_id integer NOT NULL,
  commute_distance_miles integer NOT NULL,
  label integer NOT NULL,
  censor integer NOT NULL,
  version integer NOT NULL
);

ALTER TABLE model_training_set OWNER TO inferyx;
