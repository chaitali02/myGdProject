create table "dim_transaction_type" 
   (
  "transaction_type_id" varchar2(70 byte) not null enable,
  "src_transaction_type_id"  varchar2(70 byte) not null,
  "transaction_type_code" varchar2(70 byte) not null,
  "transaction_type_desc" varchar2(70 byte) not null,
  "load_date" varchar2(70 byte) not null,
  "load_id" number(30,0),
  constraint "dim_transaction_type_id_pk" primary key ("transaction_type_id", "load_id", "load_date"),
  constraint "src_transaction_type_id_uk" unique ("src_transaction_type_id", "load_date", "load_id")
  );