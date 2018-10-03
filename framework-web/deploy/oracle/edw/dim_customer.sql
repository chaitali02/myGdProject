create table "dim_customer" 
   (
   	"customer_id" varchar2(70 byte) not null enable, 
	"src_customer_id" varchar2(70 byte), 
	"title" varchar2(70 byte), 
	"first_name" varchar2(70 byte), 
	"middle_name" varchar2(70 byte), 
	"last_name" varchar2(70 byte), 
	"address_line1" varchar2(70 byte), 
	"address_line2" varchar2(70 byte), 
	"phone" varchar2(70 byte), 
	"date_first_purchase" varchar2(70 byte), 
	"commute_distance" number(30,0), 
	"city" varchar2(70 byte), 
	"state" varchar2(70 byte), 
	"postal_code" varchar2(70 byte), 
	"country" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0) not null enable, 
	 constraint "dim_customer_pk" primary key ("customer_id", "load_id", "load_date"),
 	 constraint "src_customer_id_uk" unique ("src_customer_id", "load_date", "load_id")
);