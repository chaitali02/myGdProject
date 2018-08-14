CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE `portfolio_loss_simulaton_aggr`;

CREATE TABLE `portfolio_loss_simulaton_aggr` (
    `expected_loss` int(11) DEFAULT NULL,
    `value_at_risk` int(11) DEFAULT NULL,
    `economic_capital` int(11) DEFAULT NULL,
    `reporting_date` varchar(45) DEFAULT NULL  , 
    `version` int(11) DEFAULT NULL 
);

