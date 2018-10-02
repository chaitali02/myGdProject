DROP TABLE IF EXISTS `target_sim_linear_regression`;
CREATE TABLE `target_sim_linear_regression` (
  `id` int(11) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `interest_rate` double precision,
  `account_type_id` double precision,
  `account_status_id` double precision
);