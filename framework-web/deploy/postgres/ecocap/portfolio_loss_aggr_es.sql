DROP TABLE ecocap.portfolio_loss_aggr_es;

CREATE TABLE ecocap.portfolio_loss_aggr_es (
    expected_loss    numeric(30,2),
    value_at_risk    numeric(30,2),
    economic_capital numeric(30,2),
    expected_sum     numeric(30,2),
    reporting_date   text  , 
    version          integer 
);
