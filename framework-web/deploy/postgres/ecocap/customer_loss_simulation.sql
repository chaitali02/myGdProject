
DROP TABLE ecocap.customer_loss_simulation;

CREATE TABLE ecocap.customer_loss_simulation(
    cust_id text,
    iterationid integer,
    customer_loss  numeric(30,2)
    reporting_date  text,
    version integer    
);


