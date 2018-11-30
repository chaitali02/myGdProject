drop table if exists target_sim_linear_regression;
create table target_sim_linear_regression (
  id int(11) default null,
  version int(11) default null,
  interest_rate double precision,
  account_type_id double precision,
  account_status_id double precision
);