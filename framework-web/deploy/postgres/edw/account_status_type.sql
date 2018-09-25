DROP TABLE IF EXISTS account_status_type;
   
CREATE TABLE account_status_type
(
  account_status_id integer NOT NULL,
  account_status_code text,
  account_status_desc text,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT account_status_type_pkey PRIMARY KEY (account_status_id, load_date)
);

ALTER TABLE account_status_type OWNER TO inferyx;
