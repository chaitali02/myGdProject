
CREATE TABLE framework.customer (
  customer_id text ,
  address_id text ,
  branch_id integer ,
  title text ,
  first_name text ,
  middle_name text ,
  last_name text ,
  ssn text ,
  phone text ,
  date_first_purchase text ,
  commute_distance_miles integer ,
  load_date text ,
  version integer ,
  load_id integer,
  PRIMARY KEY (customer_id,load_date)
);

\copy framework.customer(customer_id,address_id,branch_id,title,first_name,middle_name,last_name,ssn,phone,date_first_purchase,commute_distance_miles,load_date)FROM /user/hive/warehouse/framework/upload/customer.csv delimiter ',' csv  header;