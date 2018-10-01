CREATE TABLE "customer_portfolio_ul_calc" (
  "cust_id1" VARCHAR2(70 BYTE) DEFAULT NULL,
  "industry1" VARCHAR2(70 BYTE) DEFAULT NULL,
  "correlation1" NUMBER(20,2) DEFAULT NULL,
  "unexpected_loss1" NUMBER(20,2) DEFAULT NULL,
  "cust_id2" NUMBER(20,2) DEFAULT NULL,
  "industry2" VARCHAR2(70 BYTE) DEFAULT NULL,
  "correlation2" NUMBER(20,2) DEFAULT NULL,
  "unexpected_loss2" NUMBER(20,2) DEFAULT NULL,
  "factor_value" NUMBER(20,2) DEFAULT NULL,
  "portfolio_ul_calc" NUMBER(20,2) DEFAULT NULL,
  "reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL,
  "version" NUMBER(30,0) DEFAULT NULL
);
