create table "transaction_type" 
   (
   	"transaction_type_id" varchar2(70 byte), 
	"transaction_type_code" varchar2(70 byte), 
	"transaction_type_desc" varchar2(70 byte), 
	"load_date" varchar2(70 byte), 
	"load_id" number(30,0),
	constraint "transaction_type_id_pk" primary key ("transaction_type_id", "load_id", "load_date")  
);
