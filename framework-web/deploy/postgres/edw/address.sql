DROP TABLE address;
   
CREATE TABLE address
(
  address_id text NOT NULL,
  address_line1 text,
  address_line2 text,
  address_line3 text,
  city text,
  county text,
  state text,
  zipcode integer,
  country text,
  latitude text,
  longitude text,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT address_pkey PRIMARY KEY (address_id, load_date)
);

ALTER TABLE address OWNER TO inferyx;
