create table "customer" 
   (
   	"customer_id" varchar2(70 byte) not null enable, 
	"address_id" varchar2(70 byte), 
	"branch_id" number(30,0), 
	"title" varchar2(70 byte), 
	"first_name" varchar2(70 byte), 
	"middle_name" varchar2(70 byte), 
	"last_name" varchar2(70 byte), 
	"ssn" varchar2(70 byte), 
	"phone" varchar2(70 byte), 
	"date_first_purchase" varchar2(70 byte), 
	"commute_distance_miles" number(30,0), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0), 
	 constraint "customer_pk" primary key ("customer_id", "load_date", "load_id")
);
