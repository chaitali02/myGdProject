create table "fact_account_summary_monthly" 
   (
   	"account_id" varchar2(70 byte) not null enable, 
	"yyyy_mm" varchar2(70 byte), 
	"total_trans_count" number(30,0), 
	"total_trans_amount_usd" number(30,0), 
	"avg_trans_amount" number(30,0), 
	"min_amount" number(30,2), 
	"max_amount" number(30,0), 
	"load_date" varchar2(70 byte) not null enable, 
	"load_id" number(30,0), 
	 constraint "fact_account_summary_month_pk" primary key ("account_id","yyyy_mm","load_date","load_id")
);

