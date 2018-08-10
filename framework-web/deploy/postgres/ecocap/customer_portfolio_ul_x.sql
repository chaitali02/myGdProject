DROP TABLE ecocap.customer_portfolio_ul_x;

CREATE TABLE  ecocap.customer_portfolio_ul_x (
cust_id  text,
industry text,
pd double precision,
exposure integer,
lgd double precision,
lgd_var integer,
correlation double precision,
sqrt_correlation double precision,
def_point double precision,
unexpected_loss double precision,
reporting_date text,
version integer
);
