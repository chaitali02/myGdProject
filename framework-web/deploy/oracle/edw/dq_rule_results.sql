create table "dq_rule_results" 
   (
   	"rowkey" varchar2(70 byte), 
	"datapoduuid" varchar2(70 byte), 
	"datapodversion" varchar2(70 byte), 
	"datapodname"  varchar2(70 byte),
	"attributeid" varchar2(70 byte), 
	"attributename" varchar2(70 byte),
	"attributevalue" varchar2(70 byte), 
	"nullcheck_pass" varchar2(70 byte), 
	"valuecheck_pass" varchar2(70 byte), 
	"rangecheck_pass" varchar2(70 byte), 
	"datatypecheck_pass" varchar2(70 byte), 
	"dataformatcheck_pass" varchar2(70 byte), 
	"lengthcheck_pass" varchar2(70 byte), 
	"refintegritycheck_pass" varchar2(70 byte), 
	"dupcheck_pass" varchar2(70 byte), 
	"customcheck_pass" varchar2(70 byte), 
	"version" varchar2(70 byte)
   );


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/DQ_RULE_RESULTS.ctl
