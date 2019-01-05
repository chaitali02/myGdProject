drop table if exists target_gen_data_uniform_dist;
create table target_gen_data_uniform_dist (
  id int(11) default null,
  col1 double precision,
  version int(11) default null
);