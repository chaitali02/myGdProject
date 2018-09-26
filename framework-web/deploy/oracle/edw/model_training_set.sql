CREATE TABLE model_training_set
(
  customer_id NUMBER(30,0),
  address_id NUMBER(30,0),
  branch_id NUMBER(30,0),
  commute_distance_miles NUMBER(30,0),
  label NUMBER(30,0),
  censor NUMBER(30,0),
  version NUMBER(30,0)
);

ALTER TABLE model_training_set OWNER TO inferyx;
