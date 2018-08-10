
DROP TABLE ecocap.customer_portfolio;

CREATE TABLE  ecocap.customer_portfolio (
cust_id text,
industry text,
pd double precision,
exposure integer,
lgd double precision,
lgd_var integer,
correlation double precision,
sqrt_correlation  double precision,
def_point double precision,
reporting_date text,
version integer
);



