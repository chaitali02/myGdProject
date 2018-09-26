CREATE TABLE "DQ_RULE_RESULTS" 
   (
   	"ROWKEY" VARCHAR2(70 BYTE), 
	"DATAPODUUID" VARCHAR2(70 BYTE), 
	"DATAPODVERSION" VARCHAR2(70 BYTE), 
	"DATAPODNAME"  VARCHAR2(70 BYTE),
	"ATTRIBUTEID" VARCHAR2(70 BYTE), 
	"ATTRIBUTENAME" VARCHAR2(70 BYTE),
	"ATTRIBUTEVALUE" VARCHAR2(70 BYTE), 
	"NULLCHECK_PASS" VARCHAR2(70 BYTE), 
	"VALUECHECK_PASS" VARCHAR2(70 BYTE), 
	"RANGECHECK_PASS" VARCHAR2(70 BYTE), 
	"DATATYPECHECK_PASS" VARCHAR2(70 BYTE), 
	"DATAFORMATCHECK_PASS" VARCHAR2(70 BYTE), 
	"LENGTHCHECK_PASS" VARCHAR2(70 BYTE), 
	"REFINTEGRITYCHECK_PASS" VARCHAR2(70 BYTE), 
	"DUPCHECK_PASS" VARCHAR2(70 BYTE), 
	"CUSTOMCHECK_PASS" VARCHAR2(70 BYTE), 
	"VERSION" VARCHAR2(70 BYTE)
   );


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/DQ_RULE_RESULTS.ctl
