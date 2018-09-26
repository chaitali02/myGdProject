CREATE TABLE "FACT_ACCOUNT_SUMMARY_MONTHLY" 
   (
   	"ACCOUNT_ID" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"YYYY_MM" VARCHAR2(70 BYTE), 
	"TOTAL_TRANS_COUNT" NUMBER(30,0), 
	"TOTAL_TRANS_AMOUNT_USD" NUMBER(30,0), 
	"AVG_TRANS_AMOUNT" NUMBER(30,0), 
	"MIN_AMOUNT" NUMBER(30,2), 
	"MAX_AMOUNT" NUMBER(30,0), 
	"LOAD_DATE" VARCHAR2(70 BYTE) NOT NULL ENABLE, 
	"LOAD_ID" NUMBER(30,0), 
	 CONSTRAINT "FACT_ACCOUNT_SUMMARY_MONTH_PK" PRIMARY KEY ("ACCOUNT_ID", "LOAD_DATE")
)



