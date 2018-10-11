DROP TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC;

CREATE TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC
             ( 
                          REPORTING_DATE        VARCHAR(50), 
                          TOP_N                 VARCHAR(50), 
                          VAR_CONTRIBUTION_PERC DECIMAL(10,2), 
                          VERSION               INTEGER 
             );
