CREATE DATABASE  IF NOT EXISTS `framework`;
USE `framework`;
DROP TABLE IF EXISTS `target_gen_data_uniform_dist`;

CREATE TABLE `target_gen_data_uniform_dist` (
  `id` int(11) DEFAULT NULL,
  `col1` double precision,
  `version` int(11) DEFAULT NULL
);
