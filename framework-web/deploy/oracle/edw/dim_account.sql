create table "dim_account" 
   (
   	"account_id" varchar2(70 byte) not null enable, 
	"src_account_id" varchar2(70 byte), 
	"account_type_code" varchar2(70 byte), 
	"account_status_code" varchar2(70 byte), 
	"product_type_code" varchar2(70 byte), 
	"pin_number" number(30,0), 
	"nationality" varchar2(70 byte), 
	"primary_iden_doc" varchar2(70 byte), 
	"primary_iden_doc_id" varchar2(70 byte), 
	"secondary_iden_doc" varchar2(70 byte), 
	"secondary_iden_doc_id" varchar2(70 byte), 
	"account_open_date" varchar2(70 byte), 
	"account_number" varchar2(70 byte), 
	"opening_balance" varchar2(70 byte), 
	"current_balance" varchar2(70 byte), 
	"overdue_balance" number(30,0), 
	"overdue_date" varchar2(70 byte), 
	"currency_code" varchar2(70 byte), 
	"interest_type" varchar2(70 byte), 
	"interest_rate" float(50), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0) not null enable, 
	 constraint "dim_account_pk" primary key ("account_id", "load_date", "load_id"),
	 constraint "src_account_id_uk" unique ("src_account_id", "load_date", "load_id")
);


exit

su oracle

sqlldr xe/admin /opt/oracle/app/controlLoadFile/dim_account.ctl
