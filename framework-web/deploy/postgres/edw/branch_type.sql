DROP TABLE branch_type;

CREATE TABLE branch_type
(
  branch_type_id integer NOT NULL,
  branch_type_code text,
  branch_type_desc text,
  load_date text NOT NULL,
  load_id integer,
  CONSTRAINT branch_type_pkey PRIMARY KEY (branch_type_id, load_date)
);

ALTER TABLE branch_type OWNER TO inferyx;

