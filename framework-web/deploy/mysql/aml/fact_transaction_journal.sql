drop table if exists fact_transaction_journal;

create table fact_transaction_journal 
(
  transaction_id varchar(50),
  direction varchar(50), 
  account_id varchar(50), 
  customer_id varchar(50), 
  transaction_type_code varchar(50), 
  transaction_date varchar(50), 
  transaction_country varchar(50), 
  sender_country varchar(50), 
  reciever_country varchar(50), 
  amount_usd decimal(10,3), 
  load_date varchar(50), 
  load_id integer(50), 
check_num integer(50), 
constraint transaction_id_pk primary key(transaction_id,direction));

