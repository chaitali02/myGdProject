DROP TABLE IF EXISTS MODEL_TRAINING_SET;

CREATE TABLE IF NOT EXISTS MODEL_TRAINING_SET
(
  CUSTOMER_ID STRING,
  ADDRESS_ID STRING,
  BRANCH_ID STRING,
  COMMUTE_DISTANCE_MILES INT,
  LABEL INT,
  CENSOR INT,
  VERSION STRING
);