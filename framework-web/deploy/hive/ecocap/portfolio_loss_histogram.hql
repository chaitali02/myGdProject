DROP TABLE IF EXISTS portfolio_loss_histogram;
CREATE  TABLE IF NOT EXISTS `portfolio_loss_histogram`(
  `reporting_date` string,
  `bucket` string, 
  `frequency` int, 
  `version` int);