DROP TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE
             ( 
                          ITERATIONID INTEGER, 
                          CUSTOMER    VARCHAR2(50), 
                          PD          DECIMAL(10,2), 
                          VERSION     INTEGER 
             );
