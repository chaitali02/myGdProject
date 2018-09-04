DROP TABLE IF EXISTS industry_factor_simulation_stage;
CREATE TABLE IF NOT EXISTS `industry_factor_simulation_stage`(
  `iteration_id` int, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `version` int);