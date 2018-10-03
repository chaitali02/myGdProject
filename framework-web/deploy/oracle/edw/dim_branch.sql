create table "dim_branch" 
   (
   	"branch_id" varchar2(70 byte) not null enable, 
	"src_branch_id" varchar2(70 byte), 
	"branch_type_code" varchar2(70 byte), 
	"branch_name" varchar2(70 byte), 
	"branch_desc" varchar2(70 byte), 
	"branch_contact_name" varchar2(70 byte), 
	"branch_contact_phone" varchar2(70 byte), 
	"branch_contact_email" varchar2(70 byte), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0), 
	 constraint "dim_branch_uk1" primary key ("branch_id", "load_date", "load_id"),
 	 constraint "src_branch_id_uk" unique ("src_branch_id", "load_date", "load_id")
);