CREATE TABLE "portfolio_loss_aggr" (
  "expected_loss" DECIMAL(10,0) DEFAULT NULL,
  "value_at_risk" DECIMAL(10,0) DEFAULT NULL,
  "economic_capital" DECIMAL(10,0) DEFAULT NULL,
  "reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL,
  "version" NUMBER(30,0) DEFAULT NULL
);
