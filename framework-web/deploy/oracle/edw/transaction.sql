CREATE TABLE "TRANSACTION" 
   (
   	"TRANSACTION_ID" VARCHAR2(70 BYTE), 
	"TRANSACTION_TYPE_ID" NUMBER(30,0), 
	"ACCOUNT_ID" VARCHAR2(70 BYTE), 
	"TRANSACTION_DATE" VARCHAR2(70 BYTE), 
	"FROM_ACCOUNT" VARCHAR2(70 BYTE), 
	"TO_ACCOUNT" VARCHAR2(70 BYTE), 
	"AMOUNT_BASE_CURR" NUMBER(30,2), 
	"AMOUNT_USD" NUMBER(30,2), 
	"CURRENCY_CODE" VARCHAR2(70 BYTE), 
	"CURRENCY_RATE" FLOAT(126), 
	"NOTES" VARCHAR2(90 BYTE), 
	"LOAD_DATE" VARCHAR2(70 BYTE), 
	"LOAD_ID" NUMBER(30,0)
);


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/TRANSACTION.ctl
