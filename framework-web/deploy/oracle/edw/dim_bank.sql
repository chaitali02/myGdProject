CREATE TABLE "DIM_BANK" 
   (
   	"BANK_ID" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"SRC_BANK_ID" VARCHAR2(70 BYTE), 
	"BANK_CODE" VARCHAR2(70 BYTE), 
	"BANK_NAME" VARCHAR2(70 BYTE), 
	"BANK_ACCOUNT_NUMBER" VARCHAR2(70 BYTE), 
	"BANK_CURRENCY_CODE" VARCHAR2(70 BYTE), 
	"BANK_CHECK_DIGITS" NUMBER(30,0), 
	"LOAD_DATE" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"LOAD_ID" NUMBER(30,0), 
	 CONSTRAINT "DIM_BANK_PK" PRIMARY KEY ("BANK_ID", "LOAD_DATE")
);
