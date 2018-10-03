
create table "fact_transaction" 
   (
   	"transaction_id" varchar2(70 byte) not null enable, 
	"src_transaction_id" varchar2(70 byte), 
	"transaction_type_id" varchar2(70 byte), 
	"trans_date_id" number(30,0), 
	"bank_id" varchar2(70 byte), 
	"branch_id" varchar2(70 byte), 
	"customer_id" varchar2(70 byte), 
	"address_id" varchar2(70 byte), 
	"account_id" varchar2(70 byte), 
	"from_account" varchar2(70 byte), 
	"to_account" varchar2(70 byte), 
	"amount_base_curr" number(30,0), 
	"amount_usd" number(30,0), 
	"currency_code" varchar2(70 byte), 
	"currency_rate" number(30,0), 
	"notes" varchar2(80 byte), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0) not null enable
);