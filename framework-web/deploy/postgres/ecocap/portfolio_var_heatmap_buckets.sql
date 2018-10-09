DROP TABLE IF EXISTS PORTFOLIO_VAR_HEATMAP_BUCKETS;

CREATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS
             ( 
                          PORTFOLIO_PD_BUCKET  VARCHAR(50), 
                          PORTFOLIO_LGD_BUCKET VARCHAR(50), 
                          VERSION              VARCHAR(50)
             );