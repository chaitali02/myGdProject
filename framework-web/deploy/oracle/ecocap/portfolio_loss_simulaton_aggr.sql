CREATE TABLE "portfolio_loss_simulaton_aggr" (
    "expected_loss" NUMBER(30,0) DEFAULT NULL,
    "value_at_risk" NUMBER(30,0) DEFAULT NULL,
    "economic_capital" NUMBER(30,0) DEFAULT NULL,
    "reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL  , 
    "version" NUMBER(30,0) DEFAULT NULL 
);
