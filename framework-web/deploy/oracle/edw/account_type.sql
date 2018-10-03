create table "account_type" 
   (
   	"account_type_id" number(30,0) default 0 not null enable, 
	"account_type_code" varchar2(70 byte), 
	"account_type_desc" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable,
	"load_id" number(30,0) default 0, 
	 constraint "account_type_pk" primary key ("account_type_id", "load_date", "load_id")
);



exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/ACCOUNT_TYPE.ctl
