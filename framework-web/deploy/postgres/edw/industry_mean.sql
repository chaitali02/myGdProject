DROP TABLE framework.industry_mean;


CREATE TABLE framework.industry_mean (
    id integer,
    mean double precision
);

Copy framework.industry_mean(id,mean)FROM '/user/hive/warehouse/framework/upload/industry_mean.csv' delimiter ',' csv  header;

