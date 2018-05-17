
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
