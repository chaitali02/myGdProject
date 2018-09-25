DROP TABLE IF EXISTS target_sim_linear_regression;

CREATE TABLE target_sim_linear_regression (
  id integer NOT NULL,
  version integer NOT NULL,
  interest_rate double precision,
  account_type_id double precision,
  account_status_id double precision
);
