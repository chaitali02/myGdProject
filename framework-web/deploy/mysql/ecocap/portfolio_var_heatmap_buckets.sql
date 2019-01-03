
drop table if exists portfolio_var_heatmap_buckets; 

create table portfolio_var_heatmap_buckets 
  ( 
     portfolio_pd_bucket  varchar(50), 
     portfolio_lgd_bucket varchar(50), 
     version              varchar(50) 
  ); 
