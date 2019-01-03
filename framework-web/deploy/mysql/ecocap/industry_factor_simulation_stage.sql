drop table if exists industry_factor_simulation_stage; 

create table industry_factor_simulation_stage 
  ( 
     iteration_id integer(50), 
     factor1      decimal(10, 2), 
     factor2      decimal(10, 2), 
     factor3      decimal(10, 2), 
     factor4      decimal(10, 2), 
     version      integer(50) 
  );