 create table "account_status_type" 
   (
   	"account_status_id" number(30,0) default 0 not null enable, 
	"account_status_code" varchar2(70 byte), 
	"account_status_desc" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable,
	"load_id" number(30,0) default 0, 
	 constraint "account_status_type_pk" primary key ("account_status_id", "load_date", "load_id")
);


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/ACCOUNT_STATUS_TYPE.ctl
