DROP TABLE ecocap.customer_es_allocation;

CREATE TABLE ecocap.customer_es_allocation (
    cust_id  text,
    es_contribution  numeric(30,2),
    es_allocation    numeric(30,2),
    reporting_date  text  , 
    version integer
);


