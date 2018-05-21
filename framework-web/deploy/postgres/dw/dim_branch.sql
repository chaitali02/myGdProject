



-- Table: framework.dim_branch

 DROP TABLE framework.dim_branch;

CREATE TABLE framework.dim_branch
(
  branch_id text NOT NULL,
  src_branch_id integer,
  branch_type_code text,
  branch_name text,
  branch_desc text,
  branch_contact_name text,
  branch_contact_phone text,
  branch_contact_email text,
  load_date text NOT NULL,
  load_id integer NOT NULL,
  CONSTRAINT dim_branch_pkey PRIMARY KEY (branch_id, load_date, load_id),
  CONSTRAINT src_branch_id UNIQUE (src_branch_id, load_date, load_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE framework.dim_branch
  OWNER TO inferyx;










