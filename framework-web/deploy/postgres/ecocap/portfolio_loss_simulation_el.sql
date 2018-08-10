
DROP TABLE ecocap.portfolio_loss_simulation_el;

CREATE TABLE ecocap.portfolio_loss_simulation_el (
iterationid integer,
portfolio_loss numeric(30,2),
expected_loss  numeric(30,2),
value_at_risk    numeric(30,2),
economic_capital   numeric(30,2),
reporting_date  text  , 
version integer );
