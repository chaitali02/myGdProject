create table "transaction" 
   (
   	"transaction_id" varchar2(70 byte), 
	"transaction_type_id" number(30,0), 
	"account_id" varchar2(70 byte), 
	"transaction_date" varchar2(70 byte), 
	"from_account" varchar2(70 byte), 
	"to_account" varchar2(70 byte), 
	"amount_base_curr" number(30,2), 
	"amount_usd" number(30,2), 
	"currency_code" varchar2(70 byte), 
	"currency_rate" float(126), 
	"notes" varchar2(90 byte), 
	"load_date" varchar2(70 byte), 
	"load_id" number(30,0),
	constraint "transaction_id_pk" primary key ("transaction_id", "load_id", "load_date")
);
