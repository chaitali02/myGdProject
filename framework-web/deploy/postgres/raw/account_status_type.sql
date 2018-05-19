
CREATE TABLE account_status_type (
  account_status_id integer,
  account_status_code text,
  account_status_desc text,
  load_date text,
  version text,
  load_id integer,
  PRIMARY KEY (account_status_id,load_date,load_id)
); 
