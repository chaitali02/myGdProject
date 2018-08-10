DROP TABLE ecocap.portfolio_loss_simulaton_aggr;

CREATE TABLE ecocap.portfolio_loss_simulaton_aggr (
    expected_loss  numeric(30,2),
    value_at_risk    numeric(30,2),
    economic_capital   numeric(30,2),
    reporting_date  text  , 
    version integer );
