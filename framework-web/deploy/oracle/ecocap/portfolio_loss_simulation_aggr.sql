DROP TABLE IF EXISTS portfolio_loss_simulation_aggr;
CREATE TABLE IF NOT EXISTS "portfolio_loss_simulation_aggr"(
  "expected_loss" NUMBER(20,2), 
  "value_at_risk" NUMBER(20,2), 
  "economic_capital" NUMBER(20,2),
  "reporting_date" VARCHAR2(70 BYTE),
  "version" NUMBER(30,0));
