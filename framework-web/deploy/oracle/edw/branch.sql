create table "branch" 
   (	
    "branch_id" number(30,0) not null enable, 
	"branch_type_id" number(20,0), 
	"bank_id" varchar2(70 byte), 
	"address_id" varchar2(70 byte), 
	"branch_name" varchar2(70 byte), 
	"branch_desc" varchar2(70 byte), 
	"branch_contact_name" varchar2(70 byte), 
	"branch_contact_phone" varchar2(70 byte), 
	"branch_contact_email" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable,
	"load_id" number(30,0),
	 constraint "branch_id_pk" primary key ("branch_id", "load_date", "load_id") 
);



exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/BRANCH.ctl
