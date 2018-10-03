create table "fact_customer_summary_monthly" 
   (
   	"customer_id" varchar2(70 byte) not null enable, 
	"yyyy_mm" varchar2(30 byte), 
	"total_trans_count" varchar2(70 byte), 
	"total_trans_amount_usd" number(30,0), 
	"avg_trans_amount" varchar2(70 byte), 
	"min_amount" number(30,2), 
	"max_amount" number(30,2), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0), 
	 constraint "fact_customer_summary_mont_pk" primary key ("customer_id","yyyy_mm","load_date","load_id")
);
