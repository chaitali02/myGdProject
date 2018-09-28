
CREATE TABLE  "portfolio_loss_summary" (
"portfolio_avg_pd" NUMBER(30,0) DEFAULT NULL,
"portfolio_avg_lgd" NUMBER(30,0) DEFAULT NULL,
"portfolio_total_ead" DECIMAL(10,0) DEFAULT NULL,
"portfolio_expected_loss" DECIMAL(10,0) DEFAULT NULL,
"portfolio_value_at_risk" DECIMAL(10,0) DEFAULT NULL,
"portfolio_economic_capital"  DECIMAL(10,0) DEFAULT NULL,
"portfolio_expected_sum"  DECIMAL(10,0) DEFAULT NULL,
"portfolio_es_percentage" DECIMAL(10,0) DEFAULT NULL,
"portfolio_val_percentage" DECIMAL(10,0) DEFAULT NULL,
"portfolio_el_percentage" DECIMAL(10,0) DEFAULT NULL,
"portfolio_ec_percentage" DECIMAL(10,0) DEFAULT NULL,
"reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL,
"version" NUMBER(30,0) DEFAULT NULL
);

