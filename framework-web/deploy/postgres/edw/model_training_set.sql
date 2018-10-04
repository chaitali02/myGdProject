CREATE TABLE edw_small.model_training_set
(
  customer_id integer NOT NULL,
  address_id integer NOT NULL,
  branch_id integer NOT NULL,
  commute_distance_miles integer NOT NULL,
  label integer NOT NULL,
  censor integer NOT NULL,
  version integer NOT NULL
);

