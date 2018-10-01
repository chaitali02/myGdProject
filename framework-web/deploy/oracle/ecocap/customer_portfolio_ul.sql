CREATE TABLE "customer_portfolio_ul" (
  "cust_id" VARCHAR2(70 BYTE) DEFAULT NULL,
  "industry" VARCHAR2(70 BYTE) DEFAULT NULL,
  "pd" NUMBER(20,2) DEFAULT NULL,
  "exposure" NUMBER(30,0) DEFAULT NULL,
  "lgd" NUMBER(20,2) DEFAULT NULL,
  "lgd_var" NUMBER(30,0) DEFAULT NULL,
  "correlation" NUMBER(20,2) DEFAULT NULL,
  "sqrt_correlation" NUMBER(20,2) DEFAULT NULL,
  "def_point" NUMBER(20,2) DEFAULT NULL,
  "unexpected_loss" NUMBER(20,2) DEFAULT NULL,
  "reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL,
  "version" NUMBER(30,0) DEFAULT NULL
);

