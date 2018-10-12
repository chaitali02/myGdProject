DROP TABLE IF EXISTS CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE
             ( 
                          ITERATIONID INTEGER, 
                          CUSTOMER    VARCHAR(50), 
                          PD          DECIMAL(10,2), 
                          VERSION     INTEGER 
             );
