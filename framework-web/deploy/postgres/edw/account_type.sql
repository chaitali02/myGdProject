DROP TABLE account_type;

CREATE TABLE account_type
(
  account_type_id integer NOT NULL,
  account_type_code text,
  account_type_desc text,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT account_type_pkey PRIMARY KEY (account_type_id, load_date)
);

ALTER TABLE account_type OWNER TO inferyx;

