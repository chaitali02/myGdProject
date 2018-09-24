DROP TABLE framework.industry_factor_mean;

CREATE TABLE framework.industry_factor_mean (
    id text,
    mean double precision,
    reporting_date text,
    version integer
);
Copy framework.industry_factor_mean(id,mean,reporting_date)FROM '/user/hive/warehouse/framework_bck/upload/industry_factor_mean.csv' delimiter ',' csv  header;
