
-- Table: framework.address

   DROP TABLE framework.address;


CREATE TABLE framework.address (
  address_id text ,
  address_line1 text ,
  address_line2 text ,
  address_line3 text ,
  city text ,
  county text ,
  state text ,
  zipcode integer ,
  country text ,
  latitude text ,
  longitude text ,
  load_date text ,
  version integer ,
  load_id integer,
  PRIMARY KEY (address_id,load_date)
);

Copy framework.address(address_id,address_line1,address_line2,address_line3,city,county,state,zipcode,country,latitude,longitude,load_date)FROM '/user/hive/warehouse/framework/upload/address.csv' delimiter ',' csv  header;
