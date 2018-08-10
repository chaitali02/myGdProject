DROP TABLE ecocap.portfolio_loss_summary;

CREATE TABLE  ecocap.portfolio_loss_summary (
portfolio_avg_pd integer,
portfolio_avg_lgd integer,
portfolio_total_ead double precision,
portfolio_expected_loss double precision,
portfolio_value_at_risk double precision,
portfolio_economic_capital  double precision,
portfolio_expected_sum  double precision,
portfolio_es_percentage double precision,
portfolio_val_percentage double precision,
portfolio_el_percentage double precision,
reporting_date text,
version integer
);
