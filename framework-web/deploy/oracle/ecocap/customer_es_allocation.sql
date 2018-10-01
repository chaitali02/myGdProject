DROP TABLE customer_es_allocation;
CREATE TABLE "customer_es_allocation" (
  "cust_id" VARCHAR2(70 BYTE) DEFAULT NULL,
  "es_contribution" DECIMAL(10,0) DEFAULT NULL,
  "es_allocation" DECIMAL(10,0) DEFAULT NULL,
  "reporting_date" VARCHAR2(70 BYTE) DEFAULT NULL,
  "version" NUMBER(30,0) DEFAULT NULL
);


