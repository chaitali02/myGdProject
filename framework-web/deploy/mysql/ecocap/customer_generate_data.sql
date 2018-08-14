CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE IF EXISTS `customer_generate_data`;

CREATE TABLE `customer_generate_data` (
  `id` int(11) DEFAULT NULL,
  `data` double DEFAULT NULL,
  `version` int(11) DEFAULT NULL
);


