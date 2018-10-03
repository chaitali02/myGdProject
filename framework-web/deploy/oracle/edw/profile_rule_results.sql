create table "profile_rule_results" 
   (
   	"datapoduuid" varchar2(70 byte), 
	"datapodversion" varchar2(70 byte), 
	"attributeid" varchar2(70 byte), 
	"minval" float(126), 
	"maxval" float(126), 
	"avgval" float(126), 
	"medianval" float(126), 
	"stddev" float(126), 
	"numdistinct" number(30,0), 
	"perdistinct" float(126), 
	"numnull" number(30,0), 
	"pernull" float(126), 
	"sixsigma" float(126), 
	"version" number(30,0)
   );

exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/PROFILE_RULE_RESULTS.ctl
