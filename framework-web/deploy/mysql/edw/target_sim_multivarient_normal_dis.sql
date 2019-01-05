drop table if exists target_sim_multivarient_normal_dis;
create table target_sim_multivarient_normal_dis (
  id int(11) default null,
  interestrate double precision,
  col2 double precision,
  col3 double precision,
  version int(11) default null
);