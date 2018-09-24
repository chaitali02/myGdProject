DROP TABLE IF EXISTS framework.dim_address;

CREATE TABLE framework.dim_address
(
  address_id text NOT NULL,
  src_address_id text,
  address_line1 text,
  address_line2 text,
  address_line3 text,
  city text,
  county text,
  state text,
  zipcode integer,
  country text,
  latitude text,
  longtitude text,
  load_date text NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_address_pkey PRIMARY KEY (address_id, load_date, load_id),
  CONSTRAINT src_address_id UNIQUE (src_address_id, load_date, load_id)
);

ALTER TABLE framework.dim_address OWNER TO inferyx;
