DROP TABLE framework.industry_covariance;

CREATE TABLE framework.industry_covariance (
    id integer,
    automobile double precision,
    pharma double precision,
    finance double precision    
);

Copy framework.industry_covariance(id,automobile,pharma,finance)FROM '/user/hive/warehouse/framework/upload/industry_covariance.csv' delimiter ',' csv  header;
