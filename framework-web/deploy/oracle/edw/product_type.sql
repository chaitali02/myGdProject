CREATE TABLE "PRODUCT_TYPE" 
   (
   	"PRODUCT_TYPE_ID" NUMBER(30,0), 
	"PRODUCT_TYPE_CODE" VARCHAR2(70 BYTE), 
	"PRODUCT_TYPE_DESC" VARCHAR2(70 BYTE), 
	"LOAD_DATE" VARCHAR2(70 BYTE), 
	"VERSION" NUMBER(30,0), 
	"LOAD_ID" VARCHAR2(70 BYTE)
   );


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/PRODUCT_TYPE.ctl
