CREATE DATABASE  IF NOT EXISTS `ecocap`;
USE `ecocap`;
DROP TABLE `portfolio_loss_summary`;

CREATE TABLE  `portfolio_loss_summary` (
`portfolio_avg_pd` int(11) DEFAULT NULL,
`portfolio_avg_lgd` int(11) DEFAULT NULL,
`portfolio_total_ead` decimal(10,0) DEFAULT NULL,
`portfolio_expected_loss` decimal(10,0) DEFAULT NULL,
`portfolio_value_at_risk` decimal(10,0) DEFAULT NULL,
`portfolio_economic_capital`  decimal(10,0) DEFAULT NULL,
`portfolio_expected_sum`  decimal(10,0) DEFAULT NULL,
`portfolio_es_percentage` decimal(10,0) DEFAULT NULL,
`portfolio_val_percentage` decimal(10,0) DEFAULT NULL,
`portfolio_el_percentage` decimal(10,0) DEFAULT NULL,
`reporting_date` varchar(45) DEFAULT NULL,
`version` int(11) DEFAULT NULL
);

