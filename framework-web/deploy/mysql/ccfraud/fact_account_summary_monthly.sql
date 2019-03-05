drop table if exists fact_account_summary_monthly;
create table fact_account_summary_monthly(	
	account_id varchar(50) default 0 not null,
	yyyy_mm varchar(50) default 0 not null,
	total_trans_count integer(10),
	total_trans_amount_usd integer(10),
	avg_trans_amount integer(10),
	min_amount decimal(10,2),
	max_amount integer(10),
	load_date varchar(10),
	load_id integer(50), 
constraint account_id_pk  primary key(account_id,yyyy_mm));