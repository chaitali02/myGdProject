DROP TABLE framework.product_type;

CREATE TABLE framework.product_type
(
  product_type_id integer,
  product_type_code text,
  product_type_desc text,
  load_date text,
  version integer,
  load_id text
);

Copy framework.product_type(product_type_id,product_type_code,product_type_desc,load_date)FROM '/user/hive/warehouse/framework/upload/product_type.csv' delimiter ',' csv  header;
