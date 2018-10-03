create table "dim_bank" 
   (
   	"bank_id" varchar2(70 byte) not null enable, 
	"src_bank_id" varchar2(70 byte), 
	"bank_code" varchar2(70 byte), 
	"bank_name" varchar2(70 byte), 
	"bank_account_number" varchar2(70 byte), 
	"bank_currency_code" varchar2(70 byte), 
	"bank_check_digits" number(30,0), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0), 
	 constraint "dim_bank_pk" primary key ("bank_id",  "load_date", "load_id"),
 	 constraint "src_bank_id_uk" unique ("src_bank_id", "load_date", "load_id")
);