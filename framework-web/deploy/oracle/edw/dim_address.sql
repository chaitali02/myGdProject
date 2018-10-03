create table "dim_address" 
   (
   	"address_id" varchar2(70 byte) not null enable, 
	"src_address_id" varchar2(70 byte), 
	"address_line1" varchar2(70 byte), 
	"address_line2" varchar2(70 byte), 
	"address_line3" varchar2(70 byte), 
	"city" varchar2(70 byte), 
	"county" varchar2(70 byte), 
	"state" varchar2(70 byte), 
	"zipcode" number(30,0), 
	"country" varchar2(70 byte), 
	"latitude" varchar2(70 byte), 
	"longtitude" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0) default 0, 
	 constraint "dim_address_pk" primary key ("address_id", "load_date", "load_id"),
         constraint "src_address_id_uk" unique ("src_address_id", "load_date", "load_id")
 );
