
DROP TABLE IF EXISTS PORTFOLIO_VAR_HEATMAP_BUCKETS; 

CREATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS 
  ( 
     PORTFOLIO_PD_BUCKET  STRING, 
     PORTFOLIO_LGD_BUCKET STRING, 
     VERSION              STRING 
  ) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','; 