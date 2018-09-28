CREATE TABLE "industry_factor_correlation_transpose" (
  "factor_x" VARCHAR2(70 BYTE) DEFAULT NULL,
  "reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL,
  "factor_y" VARCHAR2(70 BYTE) DEFAULT NULL,
  "factor_value" NUMBER(20,2) DEFAULT NULL,
  "version" NUMBER(30,0) DEFAULT NULL
);