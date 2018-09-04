DROP TABLE IF EXISTS portfolio_expected_sum;
CREATE  TABLE IF NOT EXISTS `portfolio_expected_sum`(
  `expected_sum` decimal,
  `reporting_date` string,
  `version` int);