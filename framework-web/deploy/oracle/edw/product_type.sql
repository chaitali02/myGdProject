
create table "product_type" 
   (
   	"product_type_id" number(30,0), 
	"product_type_code" varchar2(70 byte), 
	"product_type_desc" varchar2(70 byte), 
	"load_date" varchar2(70 byte), 
         "load_id" varchar2(70 byte),
	constraint "product_type_id_pk" primary key ("product_type_id", "load_id", "load_date")
   );
