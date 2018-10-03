create table "branch_type" 
   (
   	"branch_type_id" number(30,0) not null enable, 
	"branch_type_code" varchar2(70 byte), 
	"branch_type_desc" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0) default 0, 
	 constraint "branch_type_pk" primary key ("branch_type_id", "load_date", "load_id") 
);
exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/BRANCH_TYPE.ctl
