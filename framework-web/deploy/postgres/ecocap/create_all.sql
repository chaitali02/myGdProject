DROP TABLE CUSTOMER_ES_ALLOCATION;

CREATE TABLE CUSTOMER_ES_ALLOCATION 
             ( 
                          CUST_ID         VARCHAR(50), 
                          ES_CONTRIBUTION DECIMAL(10,2), 
                          ES_ALLOCATION   DECIMAL(10,2), 
                          REPORTING_DATE  VARCHAR(50), 
                          VERSION         INTEGER 
             );DROP TABLE CUSTOMER_GENERATE_DATA;

CREATE TABLE CUSTOMER_GENERATE_DATA 
             ( 
                          ID      INTEGER, 
                          DATA    DECIMAL(10,2), 
                          VERSION INTEGER 
             );
DROP TABLE CUSTOMER_IDIOSYNCRATIC_RISK;

CREATE TABLE  CUSTOMER_IDIOSYNCRATIC_RISK
             ( 
                          ITERATION_ID   INTEGER, 
                          CUST1          DECIMAL(10,2), 
                          CUST2          DECIMAL(10,2), 
                          CUST3          DECIMAL(10,2), 
                          CUST4          DECIMAL(10,2), 
                          CUST5          DECIMAL(10,2), 
                          CUST6          DECIMAL(10,2), 
                          CUST7          DECIMAL(10,2), 
                          CUST8          DECIMAL(10,2), 
                          CUST9          DECIMAL(10,2), 
                          CUST10         DECIMAL(10,2), 
                          CUST11         DECIMAL(10,2), 
                          CUST12         DECIMAL(10,2), 
                          CUST13         DECIMAL(10,2), 
                          CUST14         DECIMAL(10,2), 
                          CUST15         DECIMAL(10,2), 
                          CUST16         DECIMAL(10,2), 
                          CUST17         DECIMAL(10,2), 
                          CUST18         DECIMAL(10,2), 
                          CUST19         DECIMAL(10,2), 
                          CUST20         DECIMAL(10,2), 
                          CUST21         DECIMAL(10,2), 
                          CUST22         DECIMAL(10,2), 
                          CUST23         DECIMAL(10,2), 
                          CUST24         DECIMAL(10,2), 
                          CUST25         DECIMAL(10,2), 
                          CUST26         DECIMAL(10,2), 
                          CUST27         DECIMAL(10,2), 
                          CUST28         DECIMAL(10,2), 
                          CUST29         DECIMAL(10,2), 
                          CUST30         DECIMAL(10,2), 
                          CUST31         DECIMAL(10,2), 
                          CUST32         DECIMAL(10,2), 
                          CUST33         DECIMAL(10,2), 
                          CUST34         DECIMAL(10,2), 
                          CUST35         DECIMAL(10,2), 
                          CUST36         DECIMAL(10,2), 
                          CUST37         DECIMAL(10,2), 
                          CUST38         DECIMAL(10,2), 
                          CUST39         DECIMAL(10,2), 
                          CUST40         DECIMAL(10,2), 
                          CUST41         DECIMAL(10,2), 
                          CUST42         DECIMAL(10,2), 
                          CUST43         DECIMAL(10,2), 
                          CUST44         DECIMAL(10,2), 
                          CUST45         DECIMAL(10,2), 
                          CUST46         DECIMAL(10,2), 
                          CUST47         DECIMAL(10,2), 
                          CUST48         DECIMAL(10,2), 
                          CUST49         DECIMAL(10,2), 
                          CUST50         DECIMAL(10,2), 
                          CUST51         DECIMAL(10,2), 
                          CUST52         DECIMAL(10,2), 
                          CUST53         DECIMAL(10,2), 
                          CUST54         DECIMAL(10,2), 
                          CUST55         DECIMAL(10,2), 
                          CUST56         DECIMAL(10,2), 
                          CUST57         DECIMAL(10,2), 
                          CUST58         DECIMAL(10,2), 
                          CUST59         DECIMAL(10,2), 
                          CUST60         DECIMAL(10,2), 
                          CUST61         DECIMAL(10,2), 
                          CUST62         DECIMAL(10,2), 
                          CUST63         DECIMAL(10,2), 
                          CUST64         DECIMAL(10,2), 
                          CUST65         DECIMAL(10,2), 
                          CUST66         DECIMAL(10,2), 
                          CUST67         DECIMAL(10,2), 
                          CUST68         DECIMAL(10,2), 
                          CUST69         DECIMAL(10,2), 
                          CUST70         DECIMAL(10,2), 
                          CUST71         DECIMAL(10,2), 
                          CUST72         DECIMAL(10,2), 
                          CUST73         DECIMAL(10,2), 
                          CUST74         DECIMAL(10,2), 
                          CUST75         DECIMAL(10,2), 
                          CUST76         DECIMAL(10,2), 
                          CUST77         DECIMAL(10,2), 
                          CUST78         DECIMAL(10,2), 
                          CUST79         DECIMAL(10,2), 
                          CUST80         DECIMAL(10,2), 
                          CUST81         DECIMAL(10,2), 
                          CUST82         DECIMAL(10,2), 
                          CUST83         DECIMAL(10,2), 
                          CUST84         DECIMAL(10,2), 
                          CUST85         DECIMAL(10,2), 
                          CUST86         DECIMAL(10,2), 
                          CUST87         DECIMAL(10,2), 
                          CUST88         DECIMAL(10,2), 
                          CUST89         DECIMAL(10,2), 
                          CUST90         DECIMAL(10,2), 
                          CUST91         DECIMAL(10,2), 
                          CUST92         DECIMAL(10,2), 
                          CUST93         DECIMAL(10,2), 
                          CUST94         DECIMAL(10,2), 
                          CUST95         DECIMAL(10,2), 
                          CUST96         DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );


DROP TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE
             ( 
                          ITERATIONID    INTEGER, 
                          REPORTING_DATE VARCHAR(50), 
                          CUSTOMER       VARCHAR(50), 
                          PD             DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
DROP TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE
             ( 
                          ITERATIONID INTEGER, 
                          CUSTOMER    VARCHAR(50), 
                          PD          DECIMAL(10,2), 
                          VERSION     INTEGER 
             );
DROP TABLE CUSTOMER_LOSS_SIMULATION;

CREATE TABLE CUSTOMER_LOSS_SIMULATION
             ( 
                          CUST_ID        VARCHAR(50), 
                          ITERATIONID    INTEGER, 
                          CUSTOMER_LOSS  DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO_CLONE;

CREATE TABLE CUSTOMER_PORTFOLIO_CLONE
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO;

CREATE TABLE CUSTOMER_PORTFOLIO 
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION
             ( 
                          CUST_ID                      VARCHAR(50), 
                          PORTFOLIO_UL_CUST_ALLOCATION DECIMAL(10,2), 
                          REPORTING_DATE               VARCHAR(50), 
                          VERSION                      INTEGER 
             );DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC
             ( 
                          CUST_ID1          VARCHAR(50), 
                          INDUSTRY1         VARCHAR(50), 
                          CORRELATION1      DECIMAL(10,2), 
                          UNEXPECTED_LOSS1  DECIMAL(10,2), 
                          CUST_ID2          VARCHAR(50), 
                          INDUSTRY2         VARCHAR(50), 
                          CORRELATION2      DECIMAL(10,2), 
                          UNEXPECTED_LOSS2  DECIMAL(10,2), 
                          FACTOR_VALUE      DECIMAL(10,2), 
                          PORTFOLIO_UL_CALC DECIMAL(10,2), 
                          REPORTING_DATE    VARCHAR(50), 
                          VERSION           INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY
             ( 
                          CUST_ID                VARCHAR(50), 
                          PORTFOLIO_UL_CUST_SUM  DECIMAL(10,2), 
                          PORTFOLIO_UL_TOTAL_SUM DECIMAL(10,2), 
                          REPORTING_DATE         VARCHAR(50), 
                          VERSION                INTEGER 
             );DROP TABLE CUSTOMER_PORTFOLIO_UL;

CREATE TABLE CUSTOMER_PORTFOLIO_UL 
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          UNEXPECTED_LOSS  DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );DROP TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC;

CREATE TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC
             ( 
                          REPORTING_DATE        VARCHAR(50), 
                          TOP_N                 VARCHAR(50), 
                          VAR_CONTRIBUTION_PERC DECIMAL(10,2), 
                          VERSION               INTEGER 
             );
DROP TABLE INDUSTRY_FACTOR_CORRELATION;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION
             ( 
                          FACTOR         VARCHAR(50), 
                          FACTOR1        DECIMAL(10,2), 
                          FACTOR2        DECIMAL(10,2), 
                          FACTOR3        DECIMAL(10,2), 
                          FACTOR4        DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE
             ( 
                          FACTOR_X       VARCHAR(50), 
                          REPORTING_DATE VARCHAR(50), 
                          FACTOR_Y       VARCHAR(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
DROP TABLE INDUSTRY_FACTOR_MEAN;

CREATE TABLE INDUSTRY_FACTOR_MEAN 
             ( 
                          ID             VARCHAR(50), 
                          MEAN           DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_SIMULATION;

CREATE TABLE INDUSTRY_FACTOR_SIMULATION
             ( 
                          ITERATION_ID   INTEGER, 
                          FACTOR1        DECIMAL(10,2), 
                          FACTOR2        DECIMAL(10,2), 
                          FACTOR3        DECIMAL(10,2), 
                          FACTOR4        DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_SIMULATION_STAGE;

CREATE TABLE INDUSTRY_FACTOR_SIMULATION_STAGE
             ( 
                          ITERATION_ID INTEGER, 
                          FACTOR1      DECIMAL(10,2), 
                          FACTOR2      DECIMAL(10,2), 
                          FACTOR3      DECIMAL(10,2), 
                          FACTOR4      DECIMAL(10,2), 
                          VERSION      INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_TRANSPOSE
             ( 
                          ITERATION_ID   INTEGER, 
                          REPORTING_DATE VARCHAR(50), 
                          FACTOR         VARCHAR(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
DROP TABLE LKP_REPORTING_DATE;

CREATE TABLE LKP_REPORTING_DATE 
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );TRUNCATE TABLE CUSTOMER_PORTFOLIO;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION;
TRUNCATE TABLE INDUSTRY_FACTOR_MEAN; 
TRUNCATE TABLE LKP_REPORTING_DATE;
TRUNCATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

Copy CUSTOMER_PORTFOLIO FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION_TRANSPOSE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_MEAN FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy LKP_REPORTING_DATE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy PORTFOLIO_VAR_HEATMAP_BUCKETS FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

TRUNCATE TABLE CUSTOMER_PORTFOLIO;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION;
TRUNCATE TABLE INDUSTRY_FACTOR_MEAN; 
TRUNCATE TABLE LKP_REPORTING_DATE;
TRUNCATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

Copy CUSTOMER_PORTFOLIO FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION_TRANSPOSE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_MEAN FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy LKP_REPORTING_DATE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy PORTFOLIO_VAR_HEATMAP_BUCKETS FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

DROP TABLE PORTFOLIO_EXPECTED_SUM;

CREATE TABLE PORTFOLIO_EXPECTED_SUM 
             ( 
                          EXPECTED_SUM   DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_AGGR_ES;

CREATE TABLE PORTFOLIO_LOSS_AGGR_ES 
             ( 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          EXPECTED_SUM     DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          BUCKET         VARCHAR(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_HISTOGRAM;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          BUCKET         VARCHAR(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_SIMULATION_EL;

CREATE TABLE PORTFOLIO_LOSS_SIMULATION_EL
             ( 
                          ITERATIONID      INTEGER, 
                          PORTFOLIO_LOSS   DECIMAL(10,2), 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_SIMULATION;

CREATE TABLE PORTFOLIO_LOSS_SIMULATION
             ( 
                          ITERATIONID    INTEGER, 
                          PORTFOLIO_LOSS DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
DROP TABLE  PORTFOLIO_LOSS_SIMULATION_AGGR;

CREATE TABLE  PORTFOLIO_LOSS_SIMULATION_AGGR
             ( 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
DROP TABLE PORTFOLIO_LOSS_SUMMARY;

CREATE TABLE PORTFOLIO_LOSS_SUMMARY 
             ( 
                          PORTFOLIO_AVG_PD           DECIMAL(10,2), 
                          PORTFOLIO_AVG_LGD          DECIMAL(10,2), 
                          PORTFOLIO_TOTAL_EAD        DECIMAL(10,2), 
                          PORTFOLIO_EXPECTED_LOSS    DECIMAL(10,2), 
                          PORTFOLIO_VALUE_AT_RISK    DECIMAL(10,2), 
                          PORTFOLIO_ECONOMIC_CAPITAL DECIMAL(10,2), 
                          PORTFOLIO_EXPECTED_SUM     DECIMAL(10,2), 
                          PORTFOLIO_ES_PERCENTAGE    DECIMAL(10,2), 
                          PORTFOLIO_VAL_PERCENTAGE   DECIMAL(10,2), 
                          PORTFOLIO_EL_PERCENTAGE    DECIMAL(10,2), 
                          PORTFOLIO_EC_PERCENTAGE    DECIMAL(10,2), 
                          REPORTING_DATE             VARCHAR(50), 
                          VERSION                    INTEGER 
             );
DROP TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

CREATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS
             ( 
                          PORTFOLIO_PD_BUCKET  VARCHAR(50), 
                          PORTFOLIO_LGD_BUCKET VARCHAR(50), 
                          VERSION              VARCHAR(50)
             );
DROP TABLE CUSTOMER_ES_ALLOCATION;

CREATE TABLE CUSTOMER_ES_ALLOCATION 
             ( 
                          CUST_ID         VARCHAR(50), 
                          ES_CONTRIBUTION DECIMAL(10,2), 
                          ES_ALLOCATION   DECIMAL(10,2), 
                          REPORTING_DATE  VARCHAR(50), 
                          VERSION         INTEGER 
             );DROP TABLE CUSTOMER_GENERATE_DATA;

CREATE TABLE CUSTOMER_GENERATE_DATA 
             ( 
                          ID      INTEGER, 
                          DATA    DECIMAL(10,2), 
                          VERSION INTEGER 
             );
DROP TABLE CUSTOMER_IDIOSYNCRATIC_RISK;

CREATE TABLE  CUSTOMER_IDIOSYNCRATIC_RISK
             ( 
                          ITERATION_ID   INTEGER, 
                          CUST1          DECIMAL(10,2), 
                          CUST2          DECIMAL(10,2), 
                          CUST3          DECIMAL(10,2), 
                          CUST4          DECIMAL(10,2), 
                          CUST5          DECIMAL(10,2), 
                          CUST6          DECIMAL(10,2), 
                          CUST7          DECIMAL(10,2), 
                          CUST8          DECIMAL(10,2), 
                          CUST9          DECIMAL(10,2), 
                          CUST10         DECIMAL(10,2), 
                          CUST11         DECIMAL(10,2), 
                          CUST12         DECIMAL(10,2), 
                          CUST13         DECIMAL(10,2), 
                          CUST14         DECIMAL(10,2), 
                          CUST15         DECIMAL(10,2), 
                          CUST16         DECIMAL(10,2), 
                          CUST17         DECIMAL(10,2), 
                          CUST18         DECIMAL(10,2), 
                          CUST19         DECIMAL(10,2), 
                          CUST20         DECIMAL(10,2), 
                          CUST21         DECIMAL(10,2), 
                          CUST22         DECIMAL(10,2), 
                          CUST23         DECIMAL(10,2), 
                          CUST24         DECIMAL(10,2), 
                          CUST25         DECIMAL(10,2), 
                          CUST26         DECIMAL(10,2), 
                          CUST27         DECIMAL(10,2), 
                          CUST28         DECIMAL(10,2), 
                          CUST29         DECIMAL(10,2), 
                          CUST30         DECIMAL(10,2), 
                          CUST31         DECIMAL(10,2), 
                          CUST32         DECIMAL(10,2), 
                          CUST33         DECIMAL(10,2), 
                          CUST34         DECIMAL(10,2), 
                          CUST35         DECIMAL(10,2), 
                          CUST36         DECIMAL(10,2), 
                          CUST37         DECIMAL(10,2), 
                          CUST38         DECIMAL(10,2), 
                          CUST39         DECIMAL(10,2), 
                          CUST40         DECIMAL(10,2), 
                          CUST41         DECIMAL(10,2), 
                          CUST42         DECIMAL(10,2), 
                          CUST43         DECIMAL(10,2), 
                          CUST44         DECIMAL(10,2), 
                          CUST45         DECIMAL(10,2), 
                          CUST46         DECIMAL(10,2), 
                          CUST47         DECIMAL(10,2), 
                          CUST48         DECIMAL(10,2), 
                          CUST49         DECIMAL(10,2), 
                          CUST50         DECIMAL(10,2), 
                          CUST51         DECIMAL(10,2), 
                          CUST52         DECIMAL(10,2), 
                          CUST53         DECIMAL(10,2), 
                          CUST54         DECIMAL(10,2), 
                          CUST55         DECIMAL(10,2), 
                          CUST56         DECIMAL(10,2), 
                          CUST57         DECIMAL(10,2), 
                          CUST58         DECIMAL(10,2), 
                          CUST59         DECIMAL(10,2), 
                          CUST60         DECIMAL(10,2), 
                          CUST61         DECIMAL(10,2), 
                          CUST62         DECIMAL(10,2), 
                          CUST63         DECIMAL(10,2), 
                          CUST64         DECIMAL(10,2), 
                          CUST65         DECIMAL(10,2), 
                          CUST66         DECIMAL(10,2), 
                          CUST67         DECIMAL(10,2), 
                          CUST68         DECIMAL(10,2), 
                          CUST69         DECIMAL(10,2), 
                          CUST70         DECIMAL(10,2), 
                          CUST71         DECIMAL(10,2), 
                          CUST72         DECIMAL(10,2), 
                          CUST73         DECIMAL(10,2), 
                          CUST74         DECIMAL(10,2), 
                          CUST75         DECIMAL(10,2), 
                          CUST76         DECIMAL(10,2), 
                          CUST77         DECIMAL(10,2), 
                          CUST78         DECIMAL(10,2), 
                          CUST79         DECIMAL(10,2), 
                          CUST80         DECIMAL(10,2), 
                          CUST81         DECIMAL(10,2), 
                          CUST82         DECIMAL(10,2), 
                          CUST83         DECIMAL(10,2), 
                          CUST84         DECIMAL(10,2), 
                          CUST85         DECIMAL(10,2), 
                          CUST86         DECIMAL(10,2), 
                          CUST87         DECIMAL(10,2), 
                          CUST88         DECIMAL(10,2), 
                          CUST89         DECIMAL(10,2), 
                          CUST90         DECIMAL(10,2), 
                          CUST91         DECIMAL(10,2), 
                          CUST92         DECIMAL(10,2), 
                          CUST93         DECIMAL(10,2), 
                          CUST94         DECIMAL(10,2), 
                          CUST95         DECIMAL(10,2), 
                          CUST96         DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );


DROP TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE
             ( 
                          ITERATIONID    INTEGER, 
                          REPORTING_DATE VARCHAR(50), 
                          CUSTOMER       VARCHAR(50), 
                          PD             DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
DROP TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE;

CREATE TABLE CUSTOMER_IDIOSYNCRATIC_TRANSPOSE_STAGE
             ( 
                          ITERATIONID INTEGER, 
                          CUSTOMER    VARCHAR(50), 
                          PD          DECIMAL(10,2), 
                          VERSION     INTEGER 
             );
DROP TABLE CUSTOMER_LOSS_SIMULATION;

CREATE TABLE CUSTOMER_LOSS_SIMULATION
             ( 
                          CUST_ID        VARCHAR(50), 
                          ITERATIONID    INTEGER, 
                          CUSTOMER_LOSS  DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO_CLONE;

CREATE TABLE CUSTOMER_PORTFOLIO_CLONE
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO;

CREATE TABLE CUSTOMER_PORTFOLIO 
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC_ALLOCATION
             ( 
                          CUST_ID                      VARCHAR(50), 
                          PORTFOLIO_UL_CUST_ALLOCATION DECIMAL(10,2), 
                          REPORTING_DATE               VARCHAR(50), 
                          VERSION                      INTEGER 
             );DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC
             ( 
                          CUST_ID1          VARCHAR(50), 
                          INDUSTRY1         VARCHAR(50), 
                          CORRELATION1      DECIMAL(10,2), 
                          UNEXPECTED_LOSS1  DECIMAL(10,2), 
                          CUST_ID2          VARCHAR(50), 
                          INDUSTRY2         VARCHAR(50), 
                          CORRELATION2      DECIMAL(10,2), 
                          UNEXPECTED_LOSS2  DECIMAL(10,2), 
                          FACTOR_VALUE      DECIMAL(10,2), 
                          PORTFOLIO_UL_CALC DECIMAL(10,2), 
                          REPORTING_DATE    VARCHAR(50), 
                          VERSION           INTEGER 
             );
DROP TABLE CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY;

CREATE TABLE CUSTOMER_PORTFOLIO_UL_CALC_SUMMARY
             ( 
                          CUST_ID                VARCHAR(50), 
                          PORTFOLIO_UL_CUST_SUM  DECIMAL(10,2), 
                          PORTFOLIO_UL_TOTAL_SUM DECIMAL(10,2), 
                          REPORTING_DATE         VARCHAR(50), 
                          VERSION                INTEGER 
             );DROP TABLE CUSTOMER_PORTFOLIO_UL;

CREATE TABLE CUSTOMER_PORTFOLIO_UL 
             ( 
                          CUST_ID          VARCHAR(50), 
                          INDUSTRY         VARCHAR(50), 
                          PD               DECIMAL(10,2), 
                          EXPOSURE         INTEGER, 
                          LGD              DECIMAL(10,2), 
                          LGD_VAR          INTEGER, 
                          CORRELATION      DECIMAL(10,2), 
                          SQRT_CORRELATION DECIMAL(10,2), 
                          DEF_POINT        DECIMAL(10,2), 
                          UNEXPECTED_LOSS  DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );DROP TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC;

CREATE TABLE CUSTOMER_VAR_CONTRIBUTION_TOPN_PERC
             ( 
                          REPORTING_DATE        VARCHAR(50), 
                          TOP_N                 VARCHAR(50), 
                          VAR_CONTRIBUTION_PERC DECIMAL(10,2), 
                          VERSION               INTEGER 
             );
DROP TABLE INDUSTRY_FACTOR_CORRELATION;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION
             ( 
                          FACTOR         VARCHAR(50), 
                          FACTOR1        DECIMAL(10,2), 
                          FACTOR2        DECIMAL(10,2), 
                          FACTOR3        DECIMAL(10,2), 
                          FACTOR4        DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE
             ( 
                          FACTOR_X       VARCHAR(50), 
                          REPORTING_DATE VARCHAR(50), 
                          FACTOR_Y       VARCHAR(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
DROP TABLE INDUSTRY_FACTOR_MEAN;

CREATE TABLE INDUSTRY_FACTOR_MEAN 
             ( 
                          ID             VARCHAR(50), 
                          MEAN           DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_SIMULATION;

CREATE TABLE INDUSTRY_FACTOR_SIMULATION
             ( 
                          ITERATION_ID   INTEGER, 
                          FACTOR1        DECIMAL(10,2), 
                          FACTOR2        DECIMAL(10,2), 
                          FACTOR3        DECIMAL(10,2), 
                          FACTOR4        DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_SIMULATION_STAGE;

CREATE TABLE INDUSTRY_FACTOR_SIMULATION_STAGE
             ( 
                          ITERATION_ID INTEGER, 
                          FACTOR1      DECIMAL(10,2), 
                          FACTOR2      DECIMAL(10,2), 
                          FACTOR3      DECIMAL(10,2), 
                          FACTOR4      DECIMAL(10,2), 
                          VERSION      INTEGER 
             );DROP TABLE INDUSTRY_FACTOR_TRANSPOSE;

CREATE TABLE INDUSTRY_FACTOR_TRANSPOSE
             ( 
                          ITERATION_ID   INTEGER, 
                          REPORTING_DATE VARCHAR(50), 
                          FACTOR         VARCHAR(50), 
                          FACTOR_VALUE   DECIMAL(10,2), 
                          VERSION        INTEGER 
             );
DROP TABLE LKP_REPORTING_DATE;

CREATE TABLE LKP_REPORTING_DATE 
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );TRUNCATE TABLE CUSTOMER_PORTFOLIO;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION;
TRUNCATE TABLE INDUSTRY_FACTOR_MEAN; 
TRUNCATE TABLE LKP_REPORTING_DATE;
TRUNCATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

Copy CUSTOMER_PORTFOLIO FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION_TRANSPOSE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_MEAN FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy LKP_REPORTING_DATE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy PORTFOLIO_VAR_HEATMAP_BUCKETS FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

TRUNCATE TABLE CUSTOMER_PORTFOLIO;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION_TRANSPOSE;
TRUNCATE TABLE INDUSTRY_FACTOR_CORRELATION;
TRUNCATE TABLE INDUSTRY_FACTOR_MEAN; 
TRUNCATE TABLE LKP_REPORTING_DATE;
TRUNCATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

Copy CUSTOMER_PORTFOLIO FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION_TRANSPOSE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_CORRELATION FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy INDUSTRY_FACTOR_MEAN FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy LKP_REPORTING_DATE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy PORTFOLIO_VAR_HEATMAP_BUCKETS FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

DROP TABLE PORTFOLIO_EXPECTED_SUM;

CREATE TABLE PORTFOLIO_EXPECTED_SUM 
             ( 
                          EXPECTED_SUM   DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_AGGR_ES;

CREATE TABLE PORTFOLIO_LOSS_AGGR_ES 
             ( 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          EXPECTED_SUM     DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM_PERCENTAGE
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          BUCKET         VARCHAR(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_HISTOGRAM;

CREATE TABLE PORTFOLIO_LOSS_HISTOGRAM
             ( 
                          REPORTING_DATE VARCHAR(50), 
                          BUCKET         VARCHAR(50), 
                          FREQUENCY      INTEGER, 
                          VERSION        INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_SIMULATION_EL;

CREATE TABLE PORTFOLIO_LOSS_SIMULATION_EL
             ( 
                          ITERATIONID      INTEGER, 
                          PORTFOLIO_LOSS   DECIMAL(10,2), 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );DROP TABLE PORTFOLIO_LOSS_SIMULATION;

CREATE TABLE PORTFOLIO_LOSS_SIMULATION
             ( 
                          ITERATIONID    INTEGER, 
                          PORTFOLIO_LOSS DECIMAL(10,2), 
                          REPORTING_DATE VARCHAR(50), 
                          VERSION        INTEGER 
             );
DROP TABLE  PORTFOLIO_LOSS_SIMULATION_AGGR;

CREATE TABLE  PORTFOLIO_LOSS_SIMULATION_AGGR
             ( 
                          EXPECTED_LOSS    DECIMAL(10,2), 
                          VALUE_AT_RISK    DECIMAL(10,2), 
                          ECONOMIC_CAPITAL DECIMAL(10,2), 
                          REPORTING_DATE   VARCHAR(50), 
                          VERSION          INTEGER 
             );
DROP TABLE PORTFOLIO_LOSS_SUMMARY;

CREATE TABLE PORTFOLIO_LOSS_SUMMARY 
             ( 
                          PORTFOLIO_AVG_PD           DECIMAL(10,2), 
                          PORTFOLIO_AVG_LGD          DECIMAL(10,2), 
                          PORTFOLIO_TOTAL_EAD        DECIMAL(10,2), 
                          PORTFOLIO_EXPECTED_LOSS    DECIMAL(10,2), 
                          PORTFOLIO_VALUE_AT_RISK    DECIMAL(10,2), 
                          PORTFOLIO_ECONOMIC_CAPITAL DECIMAL(10,2), 
                          PORTFOLIO_EXPECTED_SUM     DECIMAL(10,2), 
                          PORTFOLIO_ES_PERCENTAGE    DECIMAL(10,2), 
                          PORTFOLIO_VAL_PERCENTAGE   DECIMAL(10,2), 
                          PORTFOLIO_EL_PERCENTAGE    DECIMAL(10,2), 
                          PORTFOLIO_EC_PERCENTAGE    DECIMAL(10,2), 
                          REPORTING_DATE             VARCHAR(50), 
                          VERSION                    INTEGER 
             );
DROP TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS;

CREATE TABLE PORTFOLIO_VAR_HEATMAP_BUCKETS
             ( 
                          PORTFOLIO_PD_BUCKET  VARCHAR(50), 
                          PORTFOLIO_LGD_BUCKET VARCHAR(50), 
                          VERSION              VARCHAR(50)
             );