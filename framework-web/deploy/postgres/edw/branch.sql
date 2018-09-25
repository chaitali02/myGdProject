DROP TABLE branch cascade;

CREATE TABLE branch
(
  branch_id integer NOT NULL,
  branch_type_id integer,
  bank_id text,
  address_id text,
  branch_name text,
  branch_desc text,
  branch_contact_name text,
  branch_contact_phone text,
  branch_contact_email text,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT branch_pkey PRIMARY KEY (branch_id, load_date)
);

ALTER TABLE branch OWNER TO inferyx;

