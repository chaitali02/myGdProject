
DROP TABLE ecocap.customer_portfolio_ul_calc;

CREATE TABLE  ecocap.customer_portfolio_ul_calc (
cust_id1   text,
industry2  text,
correlation1 double precision,
unexpected_loss1 double precision,
cust_id2   double precision,
industry2  text,
correlation2  double precision,
unexpected_loss2 double precision,
factor_value   double precision,
portfolio_ul_calc  double precision,
reporting_date text,
version integer
);

