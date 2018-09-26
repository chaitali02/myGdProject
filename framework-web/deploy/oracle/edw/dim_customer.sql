CREATE TABLE "DIM_CUSTOMER" 
   (
   	"CUSTOMER_ID" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"SRC_CUSTOMER_ID" VARCHAR2(70 BYTE), 
	"TITLE" VARCHAR2(70 BYTE), 
	"FIRST_NAME" VARCHAR2(70 BYTE), 
	"MIDDLE_NAME" VARCHAR2(70 BYTE), 
	"LAST_NAME" VARCHAR2(70 BYTE), 
	"ADDRESS_LINE1" VARCHAR2(70 BYTE), 
	"ADDRESS_LINE2" VARCHAR2(70 BYTE), 
	"PHONE" VARCHAR2(70 BYTE), 
	"DATE_FIRST_PURCHASE" VARCHAR2(70 BYTE), 
	"COMMUTE_DISTANCE" NUMBER(30,0), 
	"CITY" VARCHAR2(70 BYTE), 
	"STATE" VARCHAR2(70 BYTE), 
	"POSTAL_CODE" VARCHAR2(70 BYTE), 
	"COUNTRY" VARCHAR2(70 BYTE), 
	"LOAD_DATE" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"LOAD_ID" NUMBER(30,0) NOT NULL ENABLE, 
	 CONSTRAINT "DIM_CUSTOMER_PK" PRIMARY KEY ("CUSTOMER_ID", "LOAD_ID", "LOAD_DATE")
);
