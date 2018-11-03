DROP TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC;

CREATE TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC
             ( 
                          REPORTING_DATE        VARCHAR2(50), 
                          TOP_N                 VARCHAR2(50), 
                          VAR_CONTRIBUTION_PERC DECIMAL(10,2), 
                          VERSION               INTEGER 
             );
